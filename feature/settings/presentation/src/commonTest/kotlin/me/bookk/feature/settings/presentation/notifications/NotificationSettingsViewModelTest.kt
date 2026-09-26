package me.bookk.feature.settings.presentation.notifications

import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.core.presentation.VmArgs
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.failOnceThenSuspend
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationSettings
import me.bookk.feature.settings.domain.api.entity.NotificationChannel
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.presentation.FakePermissionChecker
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import me.bookk.feature.settings.presentation.asManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class NotificationSettingsViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture {
        val cached = MutableStateFlow<NotificationSettings?>(null)
        val getNotificationSettings = mock<GetNotificationSettings> {
            every { flow() } returns cached
            everySuspend { refresh() } returns NotificationSettings.stub()
        }
        val updateNotificationSettings = mock<UpdateNotificationSettings>()
        val permissionChecker = FakePermissionChecker()
        val errorMapper = FakeErrorMapper()
        val saved = Capture.slot<NotificationSettings>()

        fun sut() = NotificationSettingsViewModel(
            getNotificationSettings = getNotificationSettings,
            updateNotificationSettings = updateNotificationSettings,
            permissionManager = permissionChecker.asManager(),
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )

        fun stubSaveEcho() {
            everySuspend { updateNotificationSettings(capture(saved)) } calls { (s: NotificationSettings) -> s }
        }
    }

    private fun NotificationSettings.onlyChannels(vararg channels: NotificationChannel) =
        copy(channels = this.channels.filter { it.channel in channels })

    @Test
    fun `refreshes settings on start`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getNotificationSettings.refresh() }
    }

    @Test
    fun `shows only channels present in settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub().onlyChannels(NotificationChannel.EMAIL)

        then()
        assertTrue(sut.uiState.emailEnabled.isVisible)
        assertFalse(sut.uiState.pushNotificationsEnabled.isVisible)
        assertFalse(sut.uiState.telegramEnabled.isVisible)
    }

    @Test
    fun `renders channel toggles`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub().withToggled(NotificationChannel.TELEGRAM, false).copy(appointmentEnabled = false)

        then()
        assertFalse(sut.uiState.telegramEnabled.isChecked)
        assertTrue(sut.uiState.emailEnabled.isChecked)
        assertFalse(sut.uiState.appointmentEnabled.isChecked)
    }

    @Test
    fun `saves toggled email channel`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveEcho()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub()

        whenn()
        sut.uiState.emailEnabled.onCheckedChange?.invoke(false)

        then()
        assertFalse(fixture.saved.get().channels.single { it.channel == NotificationChannel.EMAIL }.enabled)
        assertFalse(sut.uiState.emailEnabled.isChecked)
    }

    @Test
    fun `saves appointment notifications toggle`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveEcho()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub()

        whenn()
        sut.uiState.appointmentEnabled.onCheckedChange?.invoke(false)

        then()
        assertFalse(fixture.saved.get().appointmentEnabled)
    }

    @Test
    fun `reverts toggle when save fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateNotificationSettings(any()) } throws TestException()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub()
        sut.uiState.emailEnabled.isChecked = false

        whenn()
        sut.uiState.emailEnabled.onCheckedChange?.invoke(false)

        then()
        assertTrue(sut.uiState.emailEnabled.isChecked)
    }

    @Test
    fun `requests permission before enabling push`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveEcho()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub().withToggled(NotificationChannel.PUSH_NOTIFICATIONS, false)

        whenn()
        sut.uiState.pushNotificationsEnabled.onCheckedChange?.invoke(true)

        then()
        assertEquals(1, fixture.permissionChecker.requestCount)
        assertTrue(fixture.saved.get().channels.single { it.channel == NotificationChannel.PUSH_NOTIFICATIONS }.enabled)
    }

    @Test
    fun `keeps push disabled when permission is denied`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.permissionChecker.granted = false
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub().withToggled(NotificationChannel.PUSH_NOTIFICATIONS, false)
        sut.uiState.pushNotificationsEnabled.isChecked = true

        whenn()
        sut.uiState.pushNotificationsEnabled.onCheckedChange?.invoke(true)

        then()
        assertFalse(sut.uiState.pushNotificationsEnabled.isChecked)
        verifySuspend(VerifyMode.not) { fixture.updateNotificationSettings(any()) }
    }

    @Test
    fun `disables push without asking permission`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveEcho()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub()

        whenn()
        sut.uiState.pushNotificationsEnabled.onCheckedChange?.invoke(false)

        then()
        assertEquals(0, fixture.permissionChecker.requestCount)
        assertFalse(fixture.saved.get().channels.single { it.channel == NotificationChannel.PUSH_NOTIFICATIONS }.enabled)
    }

    @Test
    fun `shows mapped error when refresh fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getNotificationSettings.refresh() } throws TestException()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `shows mapped error when settings observation fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.getNotificationSettings.flow() } returns failOnceThenSuspend()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<NotificationSettingsDestinations>(NotificationSettingsDestinations.Back), sut.uiState.navigation.navigationDestination)
    }
}
