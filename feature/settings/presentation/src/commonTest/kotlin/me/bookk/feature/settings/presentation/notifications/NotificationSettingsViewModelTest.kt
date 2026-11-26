package me.bookk.feature.settings.presentation.notifications

import dev.icerock.moko.resources.desc.desc
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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import library.permissions.api.PermissionManager
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
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
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationToken
import me.bookk.feature.settings.domain.api.entity.ColorScheme
import me.bookk.feature.settings.domain.api.entity.NotificationChannel
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.domain.api.entity.Settings
import me.bookk.feature.settings.domain.api.entity.SettingsProfile
import me.bookk.feature.settings.presentation.FakePermissionChecker
import me.bookk.feature.settings.presentation.FakeSettingsStateFactory
import me.bookk.feature.settings.presentation.asManager
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
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
        val updateNotificationToken = mock<UpdateNotificationToken> {
            everySuspend { invoke() } returns Unit
        }
        val getSettings = mock<GetSettings> {
            everySuspend { invoke() } returns settingsWithEmail("anna@example.com")
        }
        val permissionChecker = FakePermissionChecker()
        var permissionManager = permissionChecker.asManager()
        val errorMapper = FakeErrorMapper()
        val saved = Capture.slot<NotificationSettings>()

        fun sut() = NotificationSettingsViewModel(
            getNotificationSettings = getNotificationSettings,
            updateNotificationSettings = updateNotificationSettings,
            updateNotificationToken = updateNotificationToken,
            permissionManager = permissionManager,
            getSettings = getSettings,
            settingsStateFactory = FakeSettingsStateFactory(),
            vmArgs = VmArgs(errorMapper),
        )

        fun withoutPushPermission() {
            permissionManager = PermissionManager(
                checkers = emptyMap(),
                fallbackChecker = permissionChecker,
            )
        }

        fun stubSaveEcho() {
            everySuspend { updateNotificationSettings(capture(saved)) } calls
                { (s: NotificationSettings) -> s }
        }
    }

    private fun NotificationSettings.withChannels(
        vararg enabled: Pair<NotificationChannel, Boolean>,
    ): NotificationSettings {
        return enabled.fold(onlyChannels(*enabled.map { it.first }.toTypedArray())) { acc, pair ->
            acc.withToggled(pair.first, pair.second)
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
        fixture.cached.value =
            NotificationSettings.stub().withToggled(
                NotificationChannel.TELEGRAM,
                false,
            ).copy(appointmentEnabled = false)

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
        assertFalse(
            fixture.saved.get().channels.single {
                it.channel == NotificationChannel.EMAIL
            }.enabled,
        )
        assertFalse(sut.uiState.emailEnabled.isChecked)
    }

    @Test
    fun `plays success haptic when notification settings are saved`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveEcho()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub()

        whenn()
        sut.uiState.emailEnabled.onCheckedChange?.invoke(false)

        then()
        assertEquals(
            1,
            sut.uiState.notifications.presentationNotification.count {
                it is PresentationNotification.SuccessHaptic
            },
        )
    }

    @Test
    fun `does not play success haptic when notification settings save fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateNotificationSettings(any()) } throws TestException()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub()
        sut.uiState.emailEnabled.isChecked = false

        whenn()
        sut.uiState.emailEnabled.onCheckedChange?.invoke(false)

        then()
        assertTrue(
            sut.uiState.notifications.presentationNotification.none {
                it is PresentationNotification.SuccessHaptic
            },
        )
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
        fixture.cached.value =
            NotificationSettings.stub().withToggled(NotificationChannel.PUSH_NOTIFICATIONS, false)

        whenn()
        sut.uiState.pushNotificationsEnabled.onCheckedChange?.invoke(true)

        then()
        assertEquals(1, fixture.permissionChecker.requestCount)
        assertTrue(
            fixture.saved.get().channels.single {
                it.channel ==
                    NotificationChannel.PUSH_NOTIFICATIONS
            }.enabled,
        )
    }

    @Test
    fun `keeps push disabled when permission is denied`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.permissionChecker.granted = false
        val sut = fixture.sut()
        fixture.cached.value =
            NotificationSettings.stub().withToggled(NotificationChannel.PUSH_NOTIFICATIONS, false)
        sut.uiState.pushNotificationsEnabled.isChecked = true

        whenn()
        sut.uiState.pushNotificationsEnabled.onCheckedChange?.invoke(true)

        then()
        assertFalse(sut.uiState.pushNotificationsEnabled.isChecked)
        verifySuspend(VerifyMode.not) { fixture.updateNotificationSettings(any()) }
    }

    @Test
    fun `uploads the push token when push is enabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveEcho()
        val sut = fixture.sut()
        fixture.cached.value =
            NotificationSettings.stub().withToggled(NotificationChannel.PUSH_NOTIFICATIONS, false)

        whenn()
        sut.uiState.pushNotificationsEnabled.onCheckedChange?.invoke(true)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.updateNotificationToken() }
    }

    @Test
    fun `does not upload the push token when permission is denied`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.permissionChecker.granted = false
        val sut = fixture.sut()
        fixture.cached.value =
            NotificationSettings.stub().withToggled(NotificationChannel.PUSH_NOTIFICATIONS, false)

        whenn()
        sut.uiState.pushNotificationsEnabled.onCheckedChange?.invoke(true)

        then()
        verifySuspend(VerifyMode.not) { fixture.updateNotificationToken() }
    }

    @Test
    fun `does not upload the push token when push is disabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveEcho()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub()

        whenn()
        sut.uiState.pushNotificationsEnabled.onCheckedChange?.invoke(false)

        then()
        verifySuspend(VerifyMode.not) { fixture.updateNotificationToken() }
    }

    @Test
    fun `keeps push enabled without an error when the push token upload fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSaveEcho()
        everySuspend { fixture.updateNotificationToken() } throws TestException()
        val sut = fixture.sut()
        fixture.cached.value =
            NotificationSettings.stub().withToggled(NotificationChannel.PUSH_NOTIFICATIONS, false)

        whenn()
        sut.uiState.pushNotificationsEnabled.onCheckedChange?.invoke(true)

        then()
        assertTrue(sut.uiState.pushNotificationsEnabled.isChecked)
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
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
        assertFalse(
            fixture.saved.get().channels.single {
                it.channel ==
                    NotificationChannel.PUSH_NOTIFICATIONS
            }.enabled,
        )
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
    fun `hides push where the platform cannot ask for the notification permission`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.withoutPushPermission()
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub()

        then()
        assertFalse(sut.uiState.pushNotificationsEnabled.isVisible)
        assertTrue(sut.uiState.emailEnabled.isVisible)
        assertTrue(sut.uiState.telegramEnabled.isVisible)
    }

    @Test
    fun `disables the switch of the only enabled channel`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub().withChannels(
            NotificationChannel.TELEGRAM to true,
            NotificationChannel.EMAIL to false,
        )

        then()
        assertFalse(sut.uiState.telegramEnabled.isEnabled)
        assertTrue(sut.uiState.emailEnabled.isEnabled)
    }

    @Test
    fun `enables every channel switch while two channels are on`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub().withChannels(
            NotificationChannel.TELEGRAM to true,
            NotificationChannel.EMAIL to false,
        )

        whenn()
        fixture.cached.value = NotificationSettings.stub().withChannels(
            NotificationChannel.TELEGRAM to true,
            NotificationChannel.EMAIL to true,
        )

        then()
        assertTrue(sut.uiState.telegramEnabled.isEnabled)
        assertTrue(sut.uiState.emailEnabled.isEnabled)
    }

    @Test
    fun `enables every channel switch while no channel is on`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub().withChannels(
            NotificationChannel.TELEGRAM to false,
            NotificationChannel.EMAIL to false,
        )

        then()
        assertTrue(sut.uiState.telegramEnabled.isEnabled)
        assertTrue(sut.uiState.emailEnabled.isEnabled)
    }

    @Test
    fun `disables the last enabled channel switch among the channels shown`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.withoutPushPermission()
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub().withChannels(
            NotificationChannel.PUSH_NOTIFICATIONS to true,
            NotificationChannel.TELEGRAM to true,
            NotificationChannel.EMAIL to false,
        )

        then()
        assertFalse(sut.uiState.telegramEnabled.isEnabled)
    }

    @Test
    fun `counts push as a channel where the platform can ask for its permission`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub().withChannels(
            NotificationChannel.PUSH_NOTIFICATIONS to true,
            NotificationChannel.TELEGRAM to true,
            NotificationChannel.EMAIL to false,
        )

        then()
        assertTrue(sut.uiState.telegramEnabled.isEnabled)
        assertTrue(sut.uiState.pushNotificationsEnabled.isEnabled)
    }

    @Test
    fun `enables the switch again when turning off the last channel fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateNotificationSettings(any()) } throws TestException()
        val sut = fixture.sut()
        fixture.cached.value = NotificationSettings.stub().withChannels(
            NotificationChannel.TELEGRAM to true,
            NotificationChannel.EMAIL to true,
        )

        whenn()
        sut.uiState.emailEnabled.onCheckedChange?.invoke(false)

        then()
        assertTrue(sut.uiState.emailEnabled.isChecked)
        assertTrue(sut.uiState.telegramEnabled.isEnabled)
    }

    @Test
    fun `shows the email address under the email channel`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals("anna@example.com".desc(), sut.uiState.emailEnabled.supportingTextRes)
    }

    @Test
    fun `shows no address under the email channel when the profile has none`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSettings() } returns settingsWithEmail(" ")

        whenn()
        val sut = fixture.sut()

        then()
        assertNull(sut.uiState.emailEnabled.supportingTextRes)
    }

    @Test
    fun `logs without a notification when the email address cannot be read`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSettings() } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertNull(sut.uiState.emailEnabled.supportingTextRes)
        assertTrue(sut.uiState.notifications.presentationNotification.isEmpty())
    }

    @Test
    fun `hides the email channel when the email address is undeliverable`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSettings() } returns
            settingsWithEmail("42@telegramundeliverable.org", isEmailDeliverable = false)
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub()
            .onlyChannels(NotificationChannel.EMAIL, NotificationChannel.TELEGRAM)

        then()
        assertFalse(sut.uiState.emailEnabled.isVisible)
        assertTrue(sut.uiState.telegramEnabled.isVisible)
    }

    @Test
    fun `locks telegram when the other enabled channel is an undeliverable email`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSettings() } returns
            settingsWithEmail("42@telegramundeliverable.org", isEmailDeliverable = false)
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub().withChannels(
            NotificationChannel.TELEGRAM to true,
            NotificationChannel.EMAIL to true,
        )

        then()
        assertTrue(sut.uiState.telegramEnabled.isChecked)
        assertFalse(sut.uiState.telegramEnabled.isEnabled)
    }

    @Test
    fun `hides the email channel when the profile cannot be read`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getSettings() } throws TestException()
        val sut = fixture.sut()

        whenn()
        fixture.cached.value = NotificationSettings.stub()
            .onlyChannels(NotificationChannel.EMAIL, NotificationChannel.TELEGRAM)

        then()
        assertFalse(sut.uiState.emailEnabled.isVisible)
        assertTrue(sut.uiState.telegramEnabled.isVisible)
    }

    @Test
    fun `shows the email channel when the profile loads after the settings`() = runUnitTest {
        given()
        val fixture = Fixture()
        val profile = CompletableDeferred<Settings>()
        everySuspend { fixture.getSettings() } calls { profile.await() }
        fixture.cached.value = NotificationSettings.stub().onlyChannels(NotificationChannel.EMAIL)
        val sut = fixture.sut()

        whenn()
        profile.complete(settingsWithEmail("anna@example.com"))

        then()
        assertTrue(sut.uiState.emailEnabled.isVisible)
    }

    @Test
    fun `describes appointment notifications and the telegram channel`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            SettingsRes.strings.settings_notifications_appointment_hint.desc(),
            sut.uiState.appointmentEnabled.supportingTextRes,
        )
        assertEquals(
            SettingsRes.strings.settings_notifications_telegram_hint.desc(),
            sut.uiState.telegramEnabled.supportingTextRes,
        )
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(
            listOf<NotificationSettingsDestinations>(NotificationSettingsDestinations.Back),
            sut.uiState.navigation.navigationDestination,
        )
    }
}

private fun settingsWithEmail(email: String, isEmailDeliverable: Boolean = true): Settings {
    return Settings(ColorScheme.SYSTEM, SettingsProfile("Anna", "Smith", email, isEmailDeliverable))
}
