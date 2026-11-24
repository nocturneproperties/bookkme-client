package me.bookk.feature.settings.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.authorization.domain.datasource.device.DeviceDataSource
import me.bookk.feature.settings.domain.api.entity.Device
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class UpdateNotificationTokenImplTest {

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
        val notificationSettingsDataSource = mock<NotificationSettingsDataSource>()
        val deviceDataSource = mock<DeviceDataSource>()
        val sut = UpdateNotificationTokenImpl(notificationSettingsDataSource, deviceDataSource)

        fun stubUpload(deviceUuid: String = "device-uuid") {
            everySuspend { deviceDataSource.getOrCreateDeviceUUID() } returns deviceUuid
            everySuspend {
                notificationSettingsDataSource.updateNotificationToken(any(), any())
            } returns Device.stub()
        }

        fun stubSave() {
            everySuspend { notificationSettingsDataSource.saveNotificationToken(any()) } returns
                Unit
        }
    }

    @Test
    fun `uploads the issued token for the current device`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSave()
        fixture.stubUpload(deviceUuid = "device-uuid")

        whenn()
        fixture.sut("fcm-token")

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.notificationSettingsDataSource.updateNotificationToken(
                "device-uuid",
                "fcm-token",
            )
        }
    }

    @Test
    fun `keeps the issued token for later uploads`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSave()
        fixture.stubUpload()

        whenn()
        fixture.sut("fcm-token")

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.notificationSettingsDataSource.saveNotificationToken("fcm-token")
        }
    }

    @Test
    fun `keeps the issued token when the upload fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSave()
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend {
            fixture.notificationSettingsDataSource.updateNotificationToken(any(), any())
        } throws IllegalStateException("network unavailable")

        whenn()
        runCatching { fixture.sut("fcm-token") }

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.notificationSettingsDataSource.saveNotificationToken("fcm-token")
        }
    }

    @Test
    fun `rethrows when the upload of the issued token fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSave()
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend {
            fixture.notificationSettingsDataSource.updateNotificationToken(any(), any())
        } throws IllegalStateException("network unavailable")

        whenn()
        val result = runCatching { fixture.sut("fcm-token") }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }

    @Test
    fun `does nothing when no token was issued`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.notificationSettingsDataSource.getNotificationToken() } returns null

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.notificationSettingsDataSource.updateNotificationToken(any(), any())
        }
    }

    @Test
    fun `uploads the stored token again`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend {
            fixture.notificationSettingsDataSource.getNotificationToken()
        } returns "stored-fcm-token"
        fixture.stubUpload(deviceUuid = "device-uuid")

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.notificationSettingsDataSource.updateNotificationToken(
                "device-uuid",
                "stored-fcm-token",
            )
        }
    }

    @Test
    fun `rethrows when the upload of the stored token fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend {
            fixture.notificationSettingsDataSource.getNotificationToken()
        } returns "stored-fcm-token"
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend {
            fixture.notificationSettingsDataSource.updateNotificationToken(any(), any())
        } throws IllegalStateException("network unavailable")

        whenn()
        val result = runCatching { fixture.sut() }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
    }

    @Test
    fun `uploads the stored token when the background data fetch runs`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend {
            fixture.notificationSettingsDataSource.getNotificationToken()
        } returns "stored-fcm-token"
        fixture.stubUpload(deviceUuid = "device-uuid")

        whenn()
        fixture.sut.doOnBackgroundDataFetch()

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.notificationSettingsDataSource.updateNotificationToken(
                "device-uuid",
                "stored-fcm-token",
            )
        }
    }
}
