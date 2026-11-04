package me.bookk.feature.authorization.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import library.device.api.DeviceFacade
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationDataSource
import me.bookk.feature.authorization.domain.datasource.registration.ServerSignUpChallenge
import me.bookk.feature.authorization.domain.entity.TokenInfo
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class CreateAccountImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val registrationDataSource = mock<RegistrationDataSource>()
        val deviceDataSource = mock<DeviceDataSource>()
        val authorizationDataSource = mock<AuthorizationDataSource>()
        val deviceFacade = mock<DeviceFacade>()
        val passKeyManager = mock<PassKeyManager>()
        val initialAppDataFetch = mock<InitialAppDataFetch>()
        val sut = CreateAccountImpl(
            registrationDataSource, deviceDataSource, authorizationDataSource,
            deviceFacade, passKeyManager, initialAppDataFetch
        )
    }

    private fun stubUserData() = CreateAccount.UserData("John", "Doe", "john@example.com")

    private fun stubChallenge() = ServerSignUpChallenge(
        requestId = "req-id", displayName = "John Doe",
        jsonChallengeData = "{}", challenge = "chal", userHandle = "handle"
    )

    private val tokenInfo = TokenInfo("access", "refresh")
    private val payload = PasskeyVerificationPayload("{}")

    private suspend fun Fixture.setupHappyPath() {
        val challenge = stubChallenge()
        everySuspend { registrationDataSource.getSignUpPasskeyChallenge(any()) } returns challenge
        everySuspend { passKeyManager.create(any()) } returns payload
        everySuspend { deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend { deviceFacade.getDeviceName() } returns "Device Name"
        everySuspend { registrationDataSource.finishRegistration(any()) } returns tokenInfo
        everySuspend { authorizationDataSource.saveAuthorizationTokens(tokenInfo) } returns Unit
        everySuspend { authorizationDataSource.invalidateClientTokens() } returns Unit
        everySuspend { initialAppDataFetch.rawFetch() } returns Unit
        everySuspend { authorizationDataSource.setAuthorizationStatus(true) } returns Unit
    }

    @Test
    fun `sets authorization status true on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut(stubUserData())

        then()
        verifySuspend { fixture.authorizationDataSource.setAuthorizationStatus(true) }
    }

    @Test
    fun `saves token info on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut(stubUserData())

        then()
        verifySuspend { fixture.authorizationDataSource.saveAuthorizationTokens(tokenInfo) }
    }

    @Test
    fun `triggers a raw initial data fetch on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut(stubUserData())

        then()
        verifySuspend { fixture.initialAppDataFetch.rawFetch() }
    }

    @Test
    fun `throws Ignore when PassKeyManager throws UserCancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.registrationDataSource.getSignUpPasskeyChallenge(any()) } returns stubChallenge()
        everySuspend { fixture.passKeyManager.create(any()) } throws PassKeyManager.Error.UserCancelled()

        whenn()
        then()
        assertFailsWith<Error.Ignore> {
            fixture.sut(stubUserData())
        }
    }

    @Test
    fun `throws EmailAlreadyExist on EMAIL_EXIST error from challenge`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.registrationDataSource.getSignUpPasskeyChallenge(any()) } throws
            Error.BusinessError(AuthErrorCodes.EMAIL_EXIST, "msg")

        whenn()
        then()
        assertFailsWith<CreateAccount.Error.EmailAlreadyExist> {
            fixture.sut(stubUserData())
        }
    }

    @Test
    fun `throws EmailAlreadyExist on USER_ALREADY_EXIST error from registration`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.registrationDataSource.getSignUpPasskeyChallenge(any()) } returns stubChallenge()
        everySuspend { fixture.passKeyManager.create(any()) } returns payload
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "uuid"
        everySuspend { fixture.deviceFacade.getDeviceName() } returns "Name"
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } throws
            Error.BusinessError(AuthErrorCodes.USER_ALREADY_EXIST, "msg")

        whenn()
        then()
        assertFailsWith<CreateAccount.Error.EmailAlreadyExist> {
            fixture.sut(stubUserData())
        }
    }

    @Test
    fun `throws AccountCreationFailed when PassKeyManager throws Unknown`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.registrationDataSource.getSignUpPasskeyChallenge(any()) } returns stubChallenge()
        everySuspend { fixture.passKeyManager.create(any()) } throws PassKeyManager.Error.Unknown(null)

        whenn()
        then()
        assertFailsWith<CreateAccount.Error.AccountCreationFailed> {
            fixture.sut(stubUserData())
        }
    }

    @Test
    fun `requests sign up challenge with trimmed user data`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut(CreateAccount.UserData("  John ", "Doe\n", " john@example.com "))

        then()
        verifySuspend {
            fixture.registrationDataSource.getSignUpPasskeyChallenge(
                matches({ "trimmed" }) { it.firstName == "John" && it.lastName == "Doe" && it.email == "john@example.com" }
            )
        }
    }

    @Test
    fun `finishes registration with trimmed user data`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut(CreateAccount.UserData("  John ", "Doe\n", " john@example.com "))

        then()
        verifySuspend {
            fixture.registrationDataSource.finishRegistration(
                matches({ "trimmed" }) {
                    it.userInfo.name == "John" && it.userInfo.lastName == "Doe" && it.userInfo.email == "john@example.com"
                }
            )
        }
    }
    @Test
    fun `maps registration error 100003 to LegalDocumentVersionUnknown`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } throws Error.BusinessError(100003, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubUserData()) }

        then()
        assertFailsWith<CreateAccount.Error.LegalDocumentVersionUnknown> { result.getOrThrow() }
    }

    @Test
    fun `maps registration error 100004 to LegalDocumentsNotAccepted`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } throws Error.BusinessError(100004, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubUserData()) }

        then()
        assertFailsWith<CreateAccount.Error.LegalDocumentsNotAccepted> { result.getOrThrow() }
    }

    @Test
    fun `maps registration error 6 to ChallengeWindowExpired`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } throws Error.BusinessError(6, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubUserData()) }

        then()
        assertFailsWith<CreateAccount.Error.ChallengeWindowExpired> { result.getOrThrow() }
    }

    @Test
    fun `maps registration error 2 to InvalidEmailFormat`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } throws Error.BusinessError(2, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubUserData()) }

        then()
        assertFailsWith<CreateAccount.Error.InvalidEmailFormat> { result.getOrThrow() }
    }

    @Test
    fun `maps registration error 4 to PasskeyVerificationFailed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } throws Error.BusinessError(4, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubUserData()) }

        then()
        assertFailsWith<CreateAccount.Error.PasskeyVerificationFailed> { result.getOrThrow() }
    }

    @Test
    fun `maps registration error 5 to AccountCreationFailed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } throws Error.BusinessError(5, "rejected")

        whenn()
        val result = runCatching { fixture.sut(stubUserData()) }

        then()
        assertFailsWith<CreateAccount.Error.AccountCreationFailed> { result.getOrThrow() }
    }

    @Test
    fun `preserves the caller accepted versions through trimming and registration`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        val documents = AcceptedLegalDocuments(privacyPolicy = "privacy-v3", termsOfService = "terms-v2")
        val userData = CreateAccount.UserData(" John ", " Doe ", " john@example.com ", documents)

        whenn()
        fixture.sut(userData)

        then()
        verifySuspend {
            fixture.registrationDataSource.finishRegistration(matches({ "accepted versions" }) {
                it.acceptedDocuments == documents
            })
        }
    }

    @Test
    fun `does not infer document acceptance for legacy callers`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut(stubUserData())

        then()
        verifySuspend {
            fixture.registrationDataSource.finishRegistration(matches({ "no inferred consent" }) {
                it.acceptedDocuments.privacyPolicy == null && it.acceptedDocuments.termsOfService == null
            })
        }
    }

    @Test
    fun `does not store tokens when legal acceptance is rejected`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()
        everySuspend { fixture.registrationDataSource.finishRegistration(any()) } throws Error.BusinessError(100004, "rejected")

        whenn()
        runCatching { fixture.sut(stubUserData()) }

        then()
        verifySuspend(dev.mokkery.verify.VerifyMode.exactly(0)) {
            fixture.authorizationDataSource.saveAuthorizationTokens(any())
        }
    }

}
