package me.bookk.feature.authorization.domain.impl

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
import library.device.api.DeviceFacade
import me.bookk.core.domain.entity.Error
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.api.InitialAppDataFetch
import me.bookk.feature.authorization.domain.api.SignInWithTelegram
import me.bookk.feature.authorization.domain.datasource.AuthErrorCodes
import me.bookk.feature.authorization.domain.datasource.authorization.AuthorizationDataSource
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.authorization.domain.entity.AcceptedLegalDocuments
import me.bookk.feature.authorization.domain.entity.TokenInfo
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class SignInWithTelegramImplTest {

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
        val initialAppDataFetch = mock<InitialAppDataFetch>()
        val sut = SignInWithTelegramImpl(
            deviceDataSource,
            authorizationDataSource,
            deviceFacade,
            initialAppDataFetch,
        )
    }

    private val initData = "auth_date=1&hash=00&signature=AA&user=%7B%22id%22%3A1%7D"
    private val tokenInfo = TokenInfo("access", "refresh")
    private val documents =
        AcceptedLegalDocuments(privacyPolicy = "2026-01", termsOfService = "2026-02")

    private suspend fun Fixture.stubDevice() {
        everySuspend { deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend { deviceFacade.getDeviceName() } returns "Telegram"
    }

    private suspend fun Fixture.stubHappyPath() {
        stubDevice()
        everySuspend { authorizationDataSource.signInWithTelegram(any()) } returns tokenInfo
        everySuspend { authorizationDataSource.saveAuthorizationTokens(tokenInfo) } returns Unit
        everySuspend { authorizationDataSource.invalidateClientTokens() } returns Unit
        everySuspend { initialAppDataFetch.rawFetch() } returns Unit
        everySuspend { authorizationDataSource.setAuthorizationStatus(true) } returns Unit
    }

    private suspend fun Fixture.stubRejection(errorCode: Int) {
        stubDevice()
        everySuspend { authorizationDataSource.signInWithTelegram(any()) } throws
            Error.BusinessError(errorCode, "rejected")
    }

    @Test
    fun `sends init data, accepted documents and device to the server`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubHappyPath()

        whenn()
        fixture.sut(initData, documents)

        then()
        verifySuspend {
            fixture.authorizationDataSource.signInWithTelegram(
                matches({ "telegram sign in data" }) {
                    it.initData == initData &&
                        it.acceptedDocuments == documents &&
                        it.deviceInfo.deviceUUID == "device-uuid" &&
                        it.deviceInfo.deviceName == "Telegram"
                },
            )
        }
    }

    @Test
    fun `sends no accepted documents when none are given`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubHappyPath()

        whenn()
        fixture.sut(initData, null)

        then()
        verifySuspend {
            fixture.authorizationDataSource.signInWithTelegram(
                matches({ "no accepted documents" }) { it.acceptedDocuments == null },
            )
        }
    }

    @Test
    fun `saves token info on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubHappyPath()

        whenn()
        fixture.sut(initData, null)

        then()
        verifySuspend { fixture.authorizationDataSource.saveAuthorizationTokens(tokenInfo) }
    }

    @Test
    fun `marks the user authorized after the initial data fetch`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubHappyPath()

        whenn()
        fixture.sut(initData, null)

        then()
        verifySuspend(VerifyMode.order) {
            fixture.authorizationDataSource.saveAuthorizationTokens(tokenInfo)
            fixture.authorizationDataSource.invalidateClientTokens()
            fixture.initialAppDataFetch.rawFetch()
            fixture.authorizationDataSource.setAuthorizationStatus(true)
        }
    }

    @Test
    fun `throws TelegramDataInvalid when the server rejects the signature`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRejection(AuthErrorCodes.TELEGRAM_DATA_INVALID)

        whenn()
        then()
        assertFailsWith<SignInWithTelegram.Error.TelegramDataInvalid> {
            fixture.sut(initData, null)
        }
    }

    @Test
    fun `throws TelegramDataExpired when the init data is too old`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRejection(AuthErrorCodes.TELEGRAM_DATA_EXPIRED)

        whenn()
        then()
        assertFailsWith<SignInWithTelegram.Error.TelegramDataExpired> {
            fixture.sut(initData, null)
        }
    }

    @Test
    fun `throws Unavailable when the server has no bot configured`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRejection(AuthErrorCodes.TELEGRAM_SIGN_IN_UNAVAILABLE)

        whenn()
        then()
        assertFailsWith<SignInWithTelegram.Error.Unavailable> { fixture.sut(initData, null) }
    }

    @Test
    fun `throws AccountCreationFailed when the new account cannot be created`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRejection(AuthErrorCodes.ACCOUNT_CREATION_FAILED)

        whenn()
        then()
        assertFailsWith<SignInWithTelegram.Error.AccountCreationFailed> {
            fixture.sut(initData, null)
        }
    }

    @Test
    fun `throws LegalDocumentsNotAccepted when a new user has not accepted the documents`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubRejection(AuthErrorCodes.LEGAL_DOCUMENTS_NOT_ACCEPTED)

            whenn()
            then()
            assertFailsWith<SignInWithTelegram.Error.LegalDocumentsNotAccepted> {
                fixture.sut(initData, null)
            }
        }

    @Test
    fun `throws LegalDocumentVersionUnknown when an accepted version is not published`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.stubRejection(AuthErrorCodes.LEGAL_DOCUMENT_VERSION_UNKNOWN)

            whenn()
            then()
            assertFailsWith<SignInWithTelegram.Error.LegalDocumentVersionUnknown> {
                fixture.sut(initData, documents)
            }
        }

    @Test
    fun `rethrows an unrecognised business error`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRejection(999)

        whenn()
        val error = assertFailsWith<Error.BusinessError> { fixture.sut(initData, null) }

        then()
        assertEquals(999, error.errorCode)
    }

    @Test
    fun `rethrows a connection error`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubDevice()
        everySuspend { fixture.authorizationDataSource.signInWithTelegram(any()) } throws
            Error.NoConnectionError(RuntimeException())

        whenn()
        then()
        assertFailsWith<Error.NoConnectionError> { fixture.sut(initData, null) }
    }

    @Test
    fun `does not save tokens when the sign in is rejected`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubRejection(AuthErrorCodes.TELEGRAM_DATA_EXPIRED)

        whenn()
        runCatching { fixture.sut(initData, null) }

        then()
        verifySuspend(VerifyMode.not) {
            fixture.authorizationDataSource.saveAuthorizationTokens(any())
        }
    }
}
