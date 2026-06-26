package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
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
        val sut = DeleteAccountImpl(authorizationDataSource, passKeyManager)
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
        val sut = Fixture()
        val challenge = stubChallenge()
        everySuspend { sut.authorizationDataSource.getAuthorizationChallenge() } returns challenge
        everySuspend { sut.passKeyManager.authorize(any()) } returns verificationPayload
        everySuspend { sut.authorizationDataSource.deleteAccount(any()) } returns Unit
        everySuspend { sut.authorizationDataSource.saveAuthorizationTokens(null) } returns Unit
        everySuspend { sut.authorizationDataSource.setAuthorizationStatus(false) } returns Unit

        whenn()
        sut.sut()

        then()
        verifySuspend { sut.authorizationDataSource.saveAuthorizationTokens(null) }
        verifySuspend { sut.authorizationDataSource.setAuthorizationStatus(false) }
    }

    @Test
    fun `throws Ignore when PassKeyManager throws UserCancelled`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        everySuspend { sut.passKeyManager.authorize(any()) } throws PassKeyManager.Error.UserCancelled()

        whenn()
        then()
        assertFailsWith<Error.Ignore> {
            sut.sut()
        }
    }

    @Test
    fun `throws AccountVerificationFailed when PassKeyManager throws CredentialsMissing`() = runUnitTest {
        given()
        val sut = Fixture()
        everySuspend { sut.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        everySuspend { sut.passKeyManager.authorize(any()) } throws PassKeyManager.Error.CredentialsMissing()

        whenn()
        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> {
            sut.sut()
        }
    }
}
