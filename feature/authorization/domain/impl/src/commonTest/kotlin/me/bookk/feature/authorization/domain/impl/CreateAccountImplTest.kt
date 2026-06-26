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
        val registrationDataSource = mockk<RegistrationDataSource>()
        val deviceDataSource = mockk<DeviceDataSource>()
        val authorizationDataSource = mockk<AuthorizationDataSource>()
        val deviceFacade = mockk<DeviceFacade>()
        val passKeyManager = mockk<PassKeyManager>()
        val initialAppDataFetch = mockk<InitialAppDataFetch>()
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
        coEvery { registrationDataSource.getSignUpPasskeyChallenge(any()) } returns challenge
        coEvery { passKeyManager.create(any()) } returns payload
        coEvery { deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        coEvery { deviceFacade.getDeviceName() } returns "Device Name"
        coEvery { registrationDataSource.finishRegistration(any()) } returns tokenInfo
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
        fixture.sut(stubUserData())

        then()
        coVerify { fixture.authorizationDataSource.setAuthorizationStatus(true) }
    }

    @Test
    fun `saves token info on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.setupHappyPath()

        whenn()
        fixture.sut(stubUserData())

        then()
        coVerify { fixture.authorizationDataSource.saveAuthorizationTokens(tokenInfo) }
    }

    @Test
    fun `throws Ignore when PassKeyManager throws UserCancelled`() = runUnitTest {
        given()
        val fixture = Fixture()
        coEvery { fixture.registrationDataSource.getSignUpPasskeyChallenge(any()) } returns stubChallenge()
        coEvery { fixture.passKeyManager.create(any()) } throws PassKeyManager.Error.UserCancelled()

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
        coEvery { fixture.registrationDataSource.getSignUpPasskeyChallenge(any()) } throws
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
        coEvery { fixture.registrationDataSource.getSignUpPasskeyChallenge(any()) } returns stubChallenge()
        coEvery { fixture.passKeyManager.create(any()) } returns payload
        coEvery { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "uuid"
        coEvery { fixture.deviceFacade.getDeviceName() } returns "Name"
        coEvery { fixture.registrationDataSource.finishRegistration(any()) } throws
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
        coEvery { fixture.registrationDataSource.getSignUpPasskeyChallenge(any()) } returns stubChallenge()
        coEvery { fixture.passKeyManager.create(any()) } throws PassKeyManager.Error.Unknown(null)

        whenn()
        then()
        assertFailsWith<CreateAccount.Error.AccountCreationFailed> {
            fixture.sut(stubUserData())
        }
    }
}
