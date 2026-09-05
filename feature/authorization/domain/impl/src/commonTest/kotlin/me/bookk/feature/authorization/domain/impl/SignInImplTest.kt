package me.bookk.feature.authorization.domain.impl

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
        val deviceDataSource = mock<DeviceDataSource>()
        val authorizationDataSource = mock<AuthorizationDataSource>()
        val deviceFacade = mock<DeviceFacade>()
        val passKeyManager = mock<PassKeyManager>()
        val initialAppDataFetch = mock<InitialAppDataFetch>()
        val sut = SignInImpl(deviceDataSource, authorizationDataSource, deviceFacade, passKeyManager, initialAppDataFetch)
    }

    private fun stubChallenge() = ServerAuthenticationChallenge(
        requestId = "req-id", challengeJson = "{}", challenge = "challenge-bytes"
    )

    private val tokenInfo = TokenInfo("access", "refresh")
    private val payload = PasskeyVerificationPayload("{}")

    private suspend fun Fixture.setupHappyPath() {
        val challenge = stubChallenge()
        everySuspend { authorizationDataSource.getAuthorizationChallenge() } returns challenge
        everySuspend { passKeyManager.authorize(any()) } returns payload
        everySuspend { deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend { deviceFacade.getDeviceName() } returns "Device Name"
        everySuspend { authorizationDataSource.verifyAuthorization(any()) } returns tokenInfo
        everySuspend { authorizationDataSource.saveAuthorizationTokens(tokenInfo) } returns Unit
        everySuspend { authorizationDataSource.invalidateClientTokens() } returns Unit
        everySuspend { initialAppDataFetch(ignoreLastFetchTimestamp = true) } returns Unit
        everySuspend { authorizationDataSource.setAuthorizationStatus(true) } returns Unit
    }

    @Test
    fun `sets authorization status true on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.authorizationDataSource.setAuthorizationStatus(true) }
    }

    @Test
    fun `saves token info on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.authorizationDataSource.saveAuthorizationTokens(tokenInfo) }
    }

    @Test
    fun `ignores the last fetch timestamp when triggering the initial data fetch`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.initialAppDataFetch(ignoreLastFetchTimestamp = true) }
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
    fun `throws NoCredentialsAvailable when PassKeyManager throws CredentialsMissing`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        everySuspend { fixture.passKeyManager.authorize(any()) } throws PassKeyManager.Error.CredentialsMissing()

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
        everySuspend { fixture.authorizationDataSource.getAuthorizationChallenge() } returns stubChallenge()
        everySuspend { fixture.passKeyManager.authorize(any()) } returns payload
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "uuid"
        everySuspend { fixture.deviceFacade.getDeviceName() } returns "Name"
        everySuspend { fixture.authorizationDataSource.verifyAuthorization(any()) } throws
            Error.BusinessError(AuthErrorCodes.PASSKEY_OWNER_NOT_FOUND, "msg")

        whenn()
        then()
        assertFailsWith<SignIn.Error.NoAccountForThisPasskey> {
            fixture.sut()
        }
    }
}
