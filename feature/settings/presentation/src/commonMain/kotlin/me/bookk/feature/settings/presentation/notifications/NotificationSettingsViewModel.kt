package me.bookk.feature.settings.presentation.notifications

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationSettings
import me.bookk.feature.settings.domain.api.entity.NotificationChannel
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.presentation.SettingsStateFactory

class NotificationSettingsViewModel(
    private val getNotificationSettings: GetNotificationSettings,
    private val updateNotificationSettings: UpdateNotificationSettings,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private var loadedSettings = NotificationSettings.stub()

    val uiState: NotificationSettingsState = settingsStateFactory.createNotificationSettingsState().setup()

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
        save(
            loadedSettings.copy(
                channels = loadedSettings.channels.map {
                    if (it.channel == channel) it.copy(enabled = enabled) else it
                }
            )
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
        settings.channels.forEach { channel ->
            when (channel.channel) {
                NotificationChannel.EMAIL -> emailEnabled.isChecked = channel.enabled
                NotificationChannel.PUSH_NOTIFICATIONS -> pushNotificationsEnabled.isChecked = channel.enabled
                NotificationChannel.TELEGRAM -> telegramEnabled.isChecked = channel.enabled
            }
        }
    }

    private fun NotificationSettingsState.setup() = apply {
        appBar.title = SettingsRes.strings.settings_notifications_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(NotificationSettingsDestinations.Back) }

        appointmentEnabled.text = SettingsRes.strings.settings_notifications_appointment_label.desc()
        appointmentEnabled.onCheckedChange = weakVMClosure { vm, v -> vm.onAppointmentEnabledChanged(v) }

        emailEnabled.text = SettingsRes.strings.settings_notifications_email_label.desc()
        emailEnabled.onCheckedChange = weakVMClosure { vm, v -> vm.onChannelChanged(NotificationChannel.EMAIL, v) }

        pushNotificationsEnabled.text = SettingsRes.strings.settings_notifications_push_label.desc()
        pushNotificationsEnabled.onCheckedChange = weakVMClosure { vm, v ->
            vm.onChannelChanged(NotificationChannel.PUSH_NOTIFICATIONS, v)
        }

        telegramEnabled.text = SettingsRes.strings.settings_notifications_telegram_label.desc()
        telegramEnabled.onCheckedChange = weakVMClosure { vm, v -> vm.onChannelChanged(NotificationChannel.TELEGRAM, v) }
    }
}
