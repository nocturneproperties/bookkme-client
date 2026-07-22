package me.bookk.feature.settings.presentation.notifications

import dev.icerock.moko.resources.desc.desc
import library.permissions.api.PermissionManager
import library.permissions.api.PermissionType
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationSettings
import me.bookk.feature.settings.domain.api.entity.NotificationChannel
import me.bookk.feature.settings.domain.api.entity.NotificationChannel.EMAIL
import me.bookk.feature.settings.domain.api.entity.NotificationChannel.PUSH_NOTIFICATIONS
import me.bookk.feature.settings.domain.api.entity.NotificationChannel.TELEGRAM
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.presentation.SettingsStateFactory

class NotificationSettingsViewModel(
    private val getNotificationSettings: GetNotificationSettings,
    private val updateNotificationSettings: UpdateNotificationSettings,
    private val permissionManager: PermissionManager,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private var loadedSettings = NotificationSettings.stub()

    val uiState = settingsStateFactory.createNotificationSettingsState().setup()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        launchCached(
            launchIn = DispatcherProvider.io,
            call = { getNotificationSettings.cached(it) },
            onComplete = {
                loadedSettings = it
                renderSettings(it)
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onAppointmentEnabledChanged(enabled: Boolean) {
        save(loadedSettings.copy(appointmentEnabled = enabled))
    }

    private fun onChannelChanged(channel: NotificationChannel, enabled: Boolean) {
        when (channel) {
            PUSH_NOTIFICATIONS -> onPushStatusChanged(enabled)
            else -> save(loadedSettings.withToggled(channel, enabled))
        }
    }

    private fun onPushStatusChanged(enabled: Boolean) {
        if (!enabled) {
            save(loadedSettings.withToggled(PUSH_NOTIFICATIONS, false))
            return
        }
        launch(
            launchIn = DispatcherProvider.main,
            call = { permissionManager.requestPermission(PermissionType.NOTIFICATIONS) },
            onComplete = { permissionGranted ->
                if (permissionGranted) {
                    save(loadedSettings.withToggled(PUSH_NOTIFICATIONS, true))
                } else {
                    renderSettings(loadedSettings)
                }
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun save(settings: NotificationSettings) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { updateNotificationSettings(settings) },
            onComplete = {
                loadedSettings = it
                renderSettings(it)
            },
            onError = { renderSettings(loadedSettings) }
        )
    }

    private fun renderSettings(settings: NotificationSettings) = with(uiState) {
        appointmentEnabled.isChecked = settings.appointmentEnabled
        emailEnabled.isVisible = settings.channels.any { it.channel == EMAIL }
        pushNotificationsEnabled.isVisible = settings.channels.any { it.channel == PUSH_NOTIFICATIONS }
        telegramEnabled.isVisible = settings.channels.any { it.channel == TELEGRAM }
        settings.channels.forEach { channel ->
            when (channel.channel) {
                EMAIL -> emailEnabled.isChecked = channel.enabled
                PUSH_NOTIFICATIONS -> pushNotificationsEnabled.isChecked = channel.enabled
                TELEGRAM -> telegramEnabled.isChecked = channel.enabled
            }
        }
    }

    private fun NotificationSettingsState.setup(): NotificationSettingsState = apply {
        appBar.title = SettingsRes.strings.settings_notifications_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(NotificationSettingsDestinations.Back)
        }

        appointmentEnabled.text =
            SettingsRes.strings.settings_notifications_appointment_label.desc()
        appointmentEnabled.onCheckedChange = weakVMClosure { vm, v ->
            vm.onAppointmentEnabledChanged(v)
        }

        emailEnabled.text = SettingsRes.strings.settings_notifications_email_label.desc()
        emailEnabled.onCheckedChange = weakVMClosure { vm, v ->
            vm.onChannelChanged(EMAIL, v)
        }

        pushNotificationsEnabled.text = SettingsRes.strings.settings_notifications_push_label.desc()
        pushNotificationsEnabled.onCheckedChange = weakVMClosure { vm, v ->
            vm.onChannelChanged(PUSH_NOTIFICATIONS, v)
        }

        telegramEnabled.text = SettingsRes.strings.settings_notifications_telegram_label.desc()
        telegramEnabled.onCheckedChange = weakVMClosure { vm, v ->
            vm.onChannelChanged(TELEGRAM, v)
        }
    }
}
