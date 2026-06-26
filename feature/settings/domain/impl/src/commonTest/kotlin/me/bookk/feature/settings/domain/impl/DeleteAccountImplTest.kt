package me.bookk.feature.settings.domain.impl

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
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
        val authorizationDataSource = mockk<AuthorizationDataSource>()
        val passKeyManager = mockk<PassKeyManager>()
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
        val fixture = Fixture()
        val challenge = stubChallenge()
        coEvery { fixture.authorizationDataSource.getAuthorizationChallenge() } returns challenge
        coEvery { fixture.passKeyManager.authorize(any()) } returns verificationPayload
        coJustRun { fixture.authorizationDataSource.deleteAccount(any()) }
        coJustRun { fixture.authorizationDataSource.saveAuthorizationTokens(null) }
        coJustRun { fixture.authorizationDataSource.setAuthorizationStatus(false) }

        whenn()
        fixture.sut()

        then()
        coVerify { fixture.authorizationDataSource.saveAuthorizationTokens(null) }
        coVerify { fixture.authorizationDataSource.setAuthorizationStatus(false) }
    }

    @Test
    fun `throws Ignore when PassKeyManager throws UserCancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        coEvery { fixture.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        coEvery { fixture.passKeyManager.authorize(any()) } throws PassKeyManager.Error.UserCancelled()

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
        coEvery { fixture.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        coEvery { fixture.passKeyManager.authorize(any()) } throws PassKeyManager.Error.CredentialsMissing()

        whenn()
        then()
        assertFailsWith<DeleteAccount.Error.AccountVerificationFailed> {
            fixture.sut()
        }
    }
}
