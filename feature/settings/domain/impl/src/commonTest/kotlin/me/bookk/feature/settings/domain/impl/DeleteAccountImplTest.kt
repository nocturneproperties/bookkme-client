package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.ServerAuthenticationChallenge
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.settings.domain.api.DeleteAccount
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteAccountImplTest {

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
        val authorizationDataSource = mock<AuthorizationDataSource>()
        val passKeyManager = mock<PassKeyManager>()
        val logOut = mock<LogOut> {
            everySuspend { invoke() } returns Unit
        }
        val sut = DeleteAccountImpl(authorizationDataSource, passKeyManager, logOut)

        fun stubVerifiedDeletion(): Fixture = apply {
            everySuspend { authorizationDataSource.getAuthorizationChallenge() } returns ServerAuthenticationChallenge(
                requestId = "req-id",
                challengeJson = "{}",
                challenge = "challenge-bytes"
            )
            everySuspend { passKeyManager.authorize(any()) } returns PasskeyVerificationPayload("{}")
            everySuspend { passKeyManager.signalAccountDeleted(any()) } returns Unit
            everySuspend { authorizationDataSource.saveAuthorizationTokens(null) } returns Unit
            everySuspend { authorizationDataSource.setAuthorizationStatus(false) } returns Unit
        }
    }

    private fun stubChallenge() = ServerAuthenticationChallenge(
        requestId = "req-id",
        challengeJson = "{}",
        challenge = "challenge-bytes"
    )

    private val verificationPayload = PasskeyVerificationPayload("{}")

    @Test
    fun `clears auth tokens and status on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        val challenge = stubChallenge()
        everySuspend { fixture.authorizationDataSource.getAuthorizationChallenge() } returns challenge
        everySuspend { fixture.passKeyManager.authorize(any()) } returns verificationPayload
        everySuspend { fixture.passKeyManager.signalAccountDeleted(any()) } returns Unit
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } returns Unit
        everySuspend { fixture.authorizationDataSource.saveAuthorizationTokens(null) } returns Unit
        everySuspend { fixture.authorizationDataSource.setAuthorizationStatus(false) } returns Unit

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.authorizationDataSource.saveAuthorizationTokens(null) }
        verifySuspend { fixture.authorizationDataSource.setAuthorizationStatus(false) }
    }

    @Test
    fun `throws Ignore when PassKeyManager throws UserCancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        everySuspend { fixture.passKeyManager.authorize(any()) } throws PassKeyManager.Error.UserCancelled()

        whenn()
        then()
        assertFailsWith<Error.Ignore> {
            fixture.sut()
        }
    }

    @Test
    fun `throws AccountVerificationFailed when PassKeyManager throws CredentialsMissing`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        everySuspend { fixture.passKeyManager.authorize(any()) } throws PassKeyManager.Error.CredentialsMissing()

        whenn()
        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> {
            fixture.sut()
        }
    }

    @Test
    fun `logs out after the account is deleted`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } returns Unit

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.logOut() }
    }

    @Test
    fun `signals the credential provider with the verified passkey after the account is deleted`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        val assertion = PasskeyVerificationPayload("{\"id\":\"credential-id\"}")
        everySuspend { fixture.passKeyManager.authorize(any()) } returns assertion
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } returns Unit

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.order) {
            fixture.authorizationDataSource.deleteAccount(any())
            fixture.passKeyManager.signalAccountDeleted(assertion)
        }
    }

    @Test
    fun `completes the deletion when signaling the credential provider fails`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } returns Unit
        everySuspend { fixture.passKeyManager.signalAccountDeleted(any()) } throws PassKeyManager.Error.Infrastructure()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.logOut() }
        verifySuspend { fixture.authorizationDataSource.saveAuthorizationTokens(null) }
        verifySuspend { fixture.authorizationDataSource.setAuthorizationStatus(false) }
    }

    @Test
    fun `does not signal the credential provider when remote deletion fails`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws
            Error.BusinessError(AuthErrorCodes.VERIFICATION_FAILED, "msg")

        whenn()
        runCatching { fixture.sut() }

        then()
        verifySuspend(VerifyMode.not) { fixture.passKeyManager.signalAccountDeleted(any()) }
    }

    @Test
    fun `throws AccountVerificationFailed when server rejects the passkey`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws
            Error.BusinessError(AuthErrorCodes.VERIFICATION_FAILED, "msg")

        whenn()
        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> {
            fixture.sut()
        }
    }

    @Test
    fun `rethrows other business errors`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws
            Error.BusinessError(UNKNOWN_ERROR_CODE, "msg")

        whenn()
        then()
        assertFailsWith<Error.BusinessError> {
            fixture.sut()
        }
    }

    @Test
    fun `keeps the session when remote deletion fails`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws
            Error.BusinessError(AuthErrorCodes.VERIFICATION_FAILED, "msg")

        whenn()
        runCatching { fixture.sut() }

        then()
        verifySuspend(VerifyMode.not) { fixture.authorizationDataSource.saveAuthorizationTokens(null) }
        verifySuspend(VerifyMode.not) { fixture.logOut() }
    }

    private companion object {
        const val UNKNOWN_ERROR_CODE = 999_999
    }
}
