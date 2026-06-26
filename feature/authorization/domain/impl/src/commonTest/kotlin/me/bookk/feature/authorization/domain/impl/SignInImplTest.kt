package me.bookk.feature.authorization.domain.impl

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
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
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.SignIn
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.authorization.ServerAuthenticationChallenge
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.entity.TokenInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class SignInImplTest {

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
        val deviceDataSource = mockk<DeviceDataSource>()
        val authorizationDataSource = mockk<AuthorizationDataSource>()
        val deviceFacade = mockk<DeviceFacade>()
        val passKeyManager = mockk<PassKeyManager>()
        val initialAppDataFetch = mockk<InitialAppDataFetch>()
        val sut = SignInImpl(deviceDataSource, authorizationDataSource, deviceFacade, passKeyManager, initialAppDataFetch)
    }

    private fun stubChallenge() = ServerAuthenticationChallenge(
        requestId = "req-id", challengeJson = "{}", challenge = "challenge-bytes"
    )

    private val tokenInfo = TokenInfo("access", "refresh")
    private val payload = PasskeyVerificationPayload("{}")

    private suspend fun Fixture.setupHappyPath() {
        val challenge = stubChallenge()
        coEvery { authorizationDataSource.getAuthorizationChallenge() } returns challenge
        coEvery { passKeyManager.authorize(any()) } returns payload
        coEvery { deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        coEvery { deviceFacade.getDeviceName() } returns "Device Name"
        coEvery { authorizationDataSource.verifyAuthorization(any()) } returns tokenInfo
        coJustRun { authorizationDataSource.saveAuthorizationTokens(tokenInfo) }
        coJustRun { authorizationDataSource.invalidateClientTokens() }
        coJustRun { initialAppDataFetch() }
        coJustRun { authorizationDataSource.setAuthorizationStatus(true) }
    }

    @Test
    fun `sets authorization status true on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut()

        then()
        coVerify { fixture.authorizationDataSource.setAuthorizationStatus(true) }
    }

    @Test
    fun `saves token info on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut()

        then()
        coVerify { fixture.authorizationDataSource.saveAuthorizationTokens(tokenInfo) }
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
    fun `throws NoCredentialsAvailable when PassKeyManager throws CredentialsMissing`() = runUnitTest {
        given()
        val fixture = Fixture()
        coEvery { fixture.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        coEvery { fixture.passKeyManager.authorize(any()) } throws PassKeyManager.Error.CredentialsMissing()

        whenn()
        then()
        assertFailsWith<SignIn.Error.NoCredentialsAvailable> {
            fixture.sut()
        }
    }

    @Test
    fun `throws NoAccountForThisPasskey on PASSKEY_OWNER_NOT_FOUND error`() = runUnitTest {
        given()
        val fixture = Fixture()
        coEvery { fixture.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        coEvery { fixture.passKeyManager.authorize(any()) } returns payload
        coEvery { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "uuid"
        coEvery { fixture.deviceFacade.getDeviceName() } returns "Name"
        coEvery { fixture.authorizationDataSource.verifyAuthorization(any()) } throws
            Error.BusinessError(AuthErrorCodes.PASSKEY_OWNER_NOT_FOUND, "msg")

        whenn()
        then()
        assertFailsWith<SignIn.Error.NoAccountForThisPasskey> {
            fixture.sut()
        }
    }
}
