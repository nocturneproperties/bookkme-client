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
    }

    @Test
    fun `updates notification token for the current device`() = runUnitTest {
        given()
        val fixture = Fixture()
        val deviceUuid = "device-uuid"
        val token = "fcm-token"
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns deviceUuid
        everySuspend {
            fixture.notificationSettingsDataSource.updateNotificationToken(deviceUuid, token)
        } returns Device.stub()
        everySuspend { fixture.notificationSettingsDataSource.savePendingNotificationToken(any()) } returns Unit

        whenn()
        fixture.sut(token)

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.notificationSettingsDataSource.updateNotificationToken(deviceUuid, token)
        }
    }

    @Test
    fun `retrieves the device uuid before sending the token`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend {
            fixture.notificationSettingsDataSource.updateNotificationToken(any(), any())
        } returns Device.stub()
        everySuspend { fixture.notificationSettingsDataSource.savePendingNotificationToken(any()) } returns Unit

        whenn()
        fixture.sut("fcm-token")

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.deviceDataSource.getOrCreateDeviceUUID() }
    }

    @Test
    fun `clears the pending token once the update succeeds`() = runUnitTest {
        given()
        val fixture = Fixture()
        val token = "fcm-token"
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend {
            fixture.notificationSettingsDataSource.updateNotificationToken(any(), any())
        } returns Device.stub()
        everySuspend { fixture.notificationSettingsDataSource.savePendingNotificationToken(any()) } returns Unit

        whenn()
        fixture.sut(token)

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.notificationSettingsDataSource.savePendingNotificationToken(null)
        }
    }

    @Test
    fun `saves the token as pending when the update fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        val token = "fcm-token"
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns "device-uuid"
        everySuspend {
            fixture.notificationSettingsDataSource.updateNotificationToken(any(), any())
        } throws IllegalStateException("network unavailable")
        everySuspend { fixture.notificationSettingsDataSource.savePendingNotificationToken(any()) } returns Unit

        whenn()
        runCatching { fixture.sut(token) }

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.notificationSettingsDataSource.savePendingNotificationToken(token)
        }
    }

    @Test
    fun `does nothing when there is no pending token`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.notificationSettingsDataSource.getPendingNotificationToken() } returns null

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.deviceDataSource.getOrCreateDeviceUUID() }
        verifySuspend(VerifyMode.exactly(0)) {
            fixture.notificationSettingsDataSource.updateNotificationToken(any(), any())
        }
    }

    @Test
    fun `retries with the pending token when one exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        val pendingToken = "pending-fcm-token"
        val deviceUuid = "device-uuid"
        everySuspend { fixture.notificationSettingsDataSource.getPendingNotificationToken() } returns pendingToken
        everySuspend { fixture.deviceDataSource.getOrCreateDeviceUUID() } returns deviceUuid
        everySuspend {
            fixture.notificationSettingsDataSource.updateNotificationToken(deviceUuid, pendingToken)
        } returns Device.stub()
        everySuspend { fixture.notificationSettingsDataSource.savePendingNotificationToken(any()) } returns Unit

        whenn()
        fixture.sut()

        then()
        verifySuspend(VerifyMode.exactly(1)) {
            fixture.notificationSettingsDataSource.updateNotificationToken(deviceUuid, pendingToken)
        }
    }
}
