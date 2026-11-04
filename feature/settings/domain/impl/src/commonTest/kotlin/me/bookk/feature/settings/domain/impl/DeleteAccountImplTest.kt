package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
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
import me.bookk.feature.authorization.domain.api.ConfirmSensitiveAction
import me.bookk.feature.authorization.domain.api.LogOut
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.entity.ActionConfirmation
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import me.bookk.feature.authorization.domain.entity.UserProfile
import me.bookk.feature.settings.domain.api.DeleteAccount
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.uuid.Uuid

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
        val userId = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val authorizationDataSource = mock<AuthorizationDataSource>()
        val passKeyManager = mock<PassKeyManager>()
        val confirmation = mock<ConfirmSensitiveAction>()
        val profile = mock<UserProfileCRUD>()
        val logOut = mock<LogOut> {
            everySuspend { invoke() } returns Unit
        }
        val sut = DeleteAccountImpl(
            authorizationDataSource = authorizationDataSource,
            passKeyManager = passKeyManager,
            logOut = logOut,
            confirmation = confirmation,
            profile = profile,
        )

        init {
            everySuspend { profile.get() } returns
                UserProfile(userId, "Jane", "Doe", "jane@example.com")
            everySuspend { confirmation(any(), any(), any()) } returns
                ActionConfirmation("confirmed", "{}")
        }

        fun stubVerifiedDeletion(): Fixture = apply {
            everySuspend { passKeyManager.signalAccountDeleted(any()) } returns Unit
            everySuspend { authorizationDataSource.saveAuthorizationTokens(null) } returns Unit
            everySuspend { authorizationDataSource.setAuthorizationStatus(false) } returns Unit
        }
    }

    @Test
    fun `clears auth tokens and status on success`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } returns Unit

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.authorizationDataSource.saveAuthorizationTokens(null) }
        verifySuspend { fixture.authorizationDataSource.setAuthorizationStatus(false) }
    }

    @Test
    fun `preserves silent cancellation during confirmation`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.confirmation(any(), any(), any()) } throws
            Error.Ignore(PassKeyManager.Error.UserCancelled())

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<Error.Ignore> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.authorizationDataSource.deleteAccount(any()) }
        verifySuspend(VerifyMode.not) { fixture.logOut() }
        verifySuspend(VerifyMode.not) { fixture.passKeyManager.signalAccountDeleted(any()) }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveAuthorizationTokens(any())
        }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.setAuthorizationStatus(any())
        }
    }

    @Test
    fun `throws AccountVerificationFailed when confirmation fails verification`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.confirmation(any(), any(), any()) } throws
            ConfirmSensitiveAction.Error.VerificationFailed()

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> { result.getOrThrow() }
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
    fun `signals the credential provider with the verified passkey after the account is deleted`() =
        runUnitTest {
            given()
            val fixture = Fixture().stubVerifiedDeletion()
            val assertion = PasskeyVerificationPayload("{\"id\":\"credential-id\"}")
            everySuspend { fixture.confirmation(any(), any(), any()) } returns
                ActionConfirmation("confirmed", assertion.jsonPayload)
            everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } returns Unit

            whenn()
            fixture.sut()

            then()
            verifySuspend(VerifyMode.order) {
                fixture.authorizationDataSource.deleteAccount(any())
                fixture.passKeyManager.signalAccountDeleted(
                    matches({ "verified passkey assertion" }) {
                        it.jsonPayload ==
                            assertion.jsonPayload
                    },
                )
            }
        }

    @Test
    fun `completes the deletion when signaling the credential provider fails`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } returns Unit
        everySuspend { fixture.passKeyManager.signalAccountDeleted(any()) } throws
            PassKeyManager.Error.Infrastructure()

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
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.passKeyManager.signalAccountDeleted(any()) }
    }

    @Test
    fun `throws AccountVerificationFailed when server rejects the passkey`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws
            Error.BusinessError(AuthErrorCodes.VERIFICATION_FAILED, "msg")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> { result.getOrThrow() }
    }

    @Test
    fun `rethrows other business errors`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws
            Error.BusinessError(UNKNOWN_ERROR_CODE, "msg")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<Error.BusinessError> { result.getOrThrow() }
    }

    @Test
    fun `keeps the session when remote deletion fails`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws
            Error.BusinessError(AuthErrorCodes.VERIFICATION_FAILED, "msg")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveAuthorizationTokens(null)
        }
        verifySuspend(VerifyMode.not) { fixture.logOut() }
    }

    private companion object {
        const val UNKNOWN_ERROR_CODE = 999_999
    }

    @Test
    fun `maps an expired confirmation to AccountVerificationFailed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubVerifiedDeletion()
        everySuspend { fixture.confirmation(any(), any(), any()) } throws
            ConfirmSensitiveAction.Error.ChallengeExpired()

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> { result.getOrThrow() }
    }

    @Test
    fun `maps a missing passkey owner to AccountVerificationFailed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubVerifiedDeletion()
        everySuspend { fixture.confirmation(any(), any(), any()) } throws
            ConfirmSensitiveAction.Error.PasskeyNotFound()

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> { result.getOrThrow() }
    }

    @Test
    fun `maps a passkey belonging to another account to AccountVerificationFailed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubVerifiedDeletion()
        everySuspend { fixture.confirmation(any(), any(), any()) } throws
            ConfirmSensitiveAction.Error.PasskeyNotOwned()

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> { result.getOrThrow() }
    }

    @Test
    fun `confirms account deletion for the caller and sends only the issued token`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } returns Unit

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.order) {
            fixture.confirmation(
                SensitiveAction.DELETE_ACCOUNT,
                Uuid.NIL,
                fixture.userId,
            )
            fixture.authorizationDataSource.deleteAccount(
                matches({ "issued confirmation token" }) { it.confirmationToken == "confirmed" },
            )
            fixture.passKeyManager.signalAccountDeleted(any())
            fixture.logOut()
            fixture.authorizationDataSource.saveAuthorizationTokens(null)
            fixture.authorizationDataSource.setAuthorizationStatus(false)
        }
        verifySuspend(VerifyMode.not) { fixture.passKeyManager.authorize(any()) }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.getAuthorizationChallenge()
        }
    }

    @Test
    fun `maps an invalid account confirmation token to AccountVerificationFailed`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws
            Error.BusinessError(12, "invalid confirmation")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.logOut() }
        verifySuspend(VerifyMode.not) { fixture.passKeyManager.signalAccountDeleted(any()) }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveAuthorizationTokens(any())
        }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.setAuthorizationStatus(any())
        }
    }

    @Test
    fun `failed confirmation neither deletes the account nor clears its session`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        everySuspend { fixture.confirmation(any(), any(), any()) } throws
            ConfirmSensitiveAction.Error.ChallengeExpired()

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> { result.getOrThrow() }
        verifySuspend(VerifyMode.not) { fixture.authorizationDataSource.deleteAccount(any()) }
        verifySuspend(VerifyMode.not) { fixture.logOut() }
        verifySuspend(VerifyMode.not) { fixture.passKeyManager.signalAccountDeleted(any()) }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveAuthorizationTokens(any())
        }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.setAuthorizationStatus(any())
        }
    }

    @Test
    fun `unrelated confirmation failures propagate without deleting the account`() = runUnitTest {
        given()
        val fixture = Fixture()
        val failure = IllegalStateException("unavailable")
        everySuspend { fixture.confirmation(any(), any(), any()) } throws failure

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertSame(failure, result.exceptionOrNull())
        verifySuspend(VerifyMode.not) { fixture.authorizationDataSource.deleteAccount(any()) }
        verifySuspend(VerifyMode.not) { fixture.logOut() }
    }

    @Test
    fun `network deletion failures propagate without clearing the session`() = runUnitTest {
        given()
        val fixture = Fixture().stubVerifiedDeletion()
        val failure = IllegalStateException("offline")
        everySuspend { fixture.authorizationDataSource.deleteAccount(any()) } throws failure

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertSame(failure, result.exceptionOrNull())
        verifySuspend(VerifyMode.not) { fixture.logOut() }
        verifySuspend(VerifyMode.not) { fixture.passKeyManager.signalAccountDeleted(any()) }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveAuthorizationTokens(any())
        }
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.setAuthorizationStatus(any())
        }
    }
}
