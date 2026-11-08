package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.sequentially
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import library.device.api.DeviceFacade
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.FinishSignUp
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import me.bookk.feature.authorization.domain.entity.EmailVerificationError
import me.bookk.feature.authorization.domain.entity.TokenInfo
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class FinishSignUpImplTest {

    private class Fixture {
        val registrationDataSource = mock<RegistrationDataSource>()
        val deviceDataSource = mock<DeviceDataSource>()
        val authorizationDataSource = mock<AuthorizationDataSource>()
        val deviceFacade = mock<DeviceFacade>()
        val passKeyManager = mock<PassKeyManager>()
        val initialAppDataFetch = mock<InitialAppDataFetch>()
        val sessionDataSource = InMemorySignUpSessionDataSource()
        val sut = FinishSignUpImpl(
            registrationDataSource,
            deviceDataSource,
            authorizationDataSource,
            deviceFacade,
            passKeyManager,
            initialAppDataFetch,
            sessionDataSource,
        )
    }

    private val tokenInfo = TokenInfo("access", "refresh")
    private val payload = PasskeyVerificationPayload("{attestation}")

    private suspend fun Fixture.setupHappyPath() {
        sessionDataSource.save(SignUpSession(stubSignUpUserData(), stubSignUpChallenge()))
        everySuspend { passKeyManager.create(any()) } returns payload
        everySuspend { deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend { deviceFacade.getDeviceName() } returns "Device Name"
        everySuspend { registrationDataSource.finishRegistration(any()) } returns tokenInfo
        everySuspend { authorizationDataSource.saveAuthorizationTokens(tokenInfo) } returns Unit
        everySuspend { authorizationDataSource.invalidateClientTokens() } returns Unit
        everySuspend { initialAppDataFetch.rawFetch() } returns Unit
        everySuspend { authorizationDataSource.setAuthorizationStatus(true) } returns Unit
    }

    private suspend fun Fixture.failRegistrationWith(code: Int) {
        setupHappyPath()
        everySuspend { registrationDataSource.finishRegistration(any()) } throws
            Error.BusinessError(code, "rejected")
    }

    @Test
    fun `sets authorization status true on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut("123456")

        then()
        verifySuspend { fixture.authorizationDataSource.setAuthorizationStatus(true) }
    }

    @Test
    fun `saves tokens and runs the initial fetch on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut("123456")

        then()
        verifySuspend { fixture.authorizationDataSource.saveAuthorizationTokens(tokenInfo) }
        verifySuspend { fixture.authorizationDataSource.invalidateClientTokens() }
        verifySuspend { fixture.initialAppDataFetch.rawFetch() }
    }

    @Test
    fun `clears the session on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut("123456")

        then()
        assertNull(fixture.sessionDataSource.session)
    }

    @Test
    fun `finishes registration with the code verification id and session data`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        val documents = AcceptedLegalDocuments("privacy-v3", "terms-v2")
        fixture.sessionDataSource.save(
            SignUpSession(
                me.bookk.feature.authorization.domain.api.StartSignUp.UserData(
                    "John",
                    "Doe",
                    "john@example.com",
                    documents,
                ),
                stubSignUpChallenge(),
            ),
        )

        whenn()
        fixture.sut("123456")

        then()
        verifySuspend {
            fixture.registrationDataSource.finishRegistration(
                matches({ "code and session" }) {
                    it.emailCode == "123456" && it.verificationId == stubVerificationId &&
                        it.requestId == "req-id" && it.acceptedDocuments == documents &&
                        it.userInfo.email == "john@example.com" &&
                        it.publicKeyCredentialJson == "{attestation}" &&
                        it.deviceInfo.deviceUUID == "device-uuid"
                },
            )
        }
    }

    @Test
    fun `throws SignUpNotStarted without a session`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<FinishSignUp.Error.SignUpNotStarted> { result.getOrThrow() }
    }

    @Test
    fun `creates the passkey once across a wrong code retry`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } sequentially {
            throws(Error.BusinessError(AuthErrorCodes.EMAIL_CODE_INVALID, "wrong"))
            returns(tokenInfo)
        }

        whenn()
        runCatching { fixture.sut("000000") }
        fixture.sut("123456")

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.passKeyManager.create(any()) }
        verifySuspend(VerifyMode.exactly(2)) {
            fixture.registrationDataSource.finishRegistration(any())
        }
    }

    @Test
    fun `keeps the session and attestation on a wrong code`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.EMAIL_CODE_INVALID)

        whenn()
        val result = runCatching { fixture.sut("000000") }

        then()
        assertFailsWith<EmailVerificationError.CodeInvalid> { result.getOrThrow() }
        assertEquals(payload, fixture.sessionDataSource.session!!.attestation)
    }

    @Test
    fun `creates a new passkey for a session started again`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.EMAIL_CODE_INVALID)
        runCatching { fixture.sut("000000") }
        fixture.sessionDataSource.save(SignUpSession(stubSignUpUserData(), stubSignUpChallenge()))

        whenn()
        runCatching { fixture.sut("000000") }

        then()
        verifySuspend(VerifyMode.exactly(2)) { fixture.passKeyManager.create(any()) }
    }

    @Test
    fun `maps code expired and drops the session`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.EMAIL_CODE_EXPIRED)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<EmailVerificationError.CodeExpired> { result.getOrThrow() }
        assertNull(fixture.sessionDataSource.session)
    }

    @Test
    fun `maps attempts exceeded and drops the session`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.EMAIL_CODE_ATTEMPTS_EXCEEDED)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<EmailVerificationError.AttemptsExceeded> { result.getOrThrow() }
        assertNull(fixture.sessionDataSource.session)
    }

    @Test
    fun `maps challenge window expired and drops the session`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.CHALLENGE_WINDOW_EXPIRED)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<FinishSignUp.Error.ChallengeWindowExpired> { result.getOrThrow() }
        assertNull(fixture.sessionDataSource.session)
    }

    @Test
    fun `maps user already exist and drops the session`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.USER_ALREADY_EXIST)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<FinishSignUp.Error.EmailAlreadyExist> { result.getOrThrow() }
        assertNull(fixture.sessionDataSource.session)
    }

    @Test
    fun `maps invalid email format`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.INVALID_EMAIL_FORMAT)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<FinishSignUp.Error.InvalidEmailFormat> { result.getOrThrow() }
    }

    @Test
    fun `maps passkey verification failed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.VERIFICATION_FAILED)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<FinishSignUp.Error.PasskeyVerificationFailed> { result.getOrThrow() }
    }

    @Test
    fun `maps account creation failed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.ACCOUNT_CREATION_FAILED)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<FinishSignUp.Error.AccountCreationFailed> { result.getOrThrow() }
    }

    @Test
    fun `maps legal document version unknown`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.LEGAL_DOCUMENT_VERSION_UNKNOWN)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<FinishSignUp.Error.LegalDocumentVersionUnknown> { result.getOrThrow() }
    }

    @Test
    fun `maps legal documents not accepted`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.LEGAL_DOCUMENTS_NOT_ACCEPTED)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<FinishSignUp.Error.LegalDocumentsNotAccepted> { result.getOrThrow() }
    }

    @Test
    fun `rethrows unrelated business errors and keeps the session`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(777)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertEquals(777, assertFailsWith<Error.BusinessError> { result.getOrThrow() }.errorCode)
        assertNotNull(fixture.sessionDataSource.session)
    }

    @Test
    fun `keeps the session when the connection fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } throws
            Error.NoConnectionError(Exception())

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<Error.NoConnectionError> { result.getOrThrow() }
        assertNotNull(fixture.sessionDataSource.session)
    }

    @Test
    fun `throws Ignore and keeps the session when the passkey prompt is cancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.passKeyManager.create(any()) } throws
            PassKeyManager.Error.UserCancelled()

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<Error.Ignore> { result.getOrThrow() }
        assertNull(fixture.sessionDataSource.session!!.attestation)
    }

    @Test
    fun `throws AccountCreationFailed when passkey creation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.passKeyManager.create(any()) } throws
            PassKeyManager.Error.Unknown(null)

        whenn()
        val result = runCatching { fixture.sut("123456") }

        then()
        assertFailsWith<FinishSignUp.Error.AccountCreationFailed> { result.getOrThrow() }
    }

    @Test
    fun `does not store tokens or authorize when registration fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.failRegistrationWith(AuthErrorCodes.EMAIL_CODE_INVALID)

        whenn()
        runCatching { fixture.sut("000000") }

        then()
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.authorizationDataSource.saveAuthorizationTokens(any())
        }
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.authorizationDataSource.setAuthorizationStatus(any())
        }
    }
}
