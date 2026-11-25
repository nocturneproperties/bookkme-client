package me.bookk.feature.settings.presentation.notifications

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flowOn
import library.permissions.api.PermissionManager
import library.permissions.api.PermissionType
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.feature.settings.domain.api.GetNotificationSettings
import me.bookk.feature.settings.domain.api.GetSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationSettings
import me.bookk.feature.settings.domain.api.UpdateNotificationToken
import me.bookk.feature.settings.domain.api.entity.NotificationChannel
import me.bookk.feature.settings.domain.api.entity.NotificationChannel.EMAIL
import me.bookk.feature.settings.domain.api.entity.NotificationChannel.PUSH_NOTIFICATIONS
import me.bookk.feature.settings.domain.api.entity.NotificationChannel.TELEGRAM
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.presentation.SettingsStateFactory

class NotificationSettingsViewModel(
    private val getNotificationSettings: GetNotificationSettings,
    private val updateNotificationSettings: UpdateNotificationSettings,
    private val updateNotificationToken: UpdateNotificationToken,
    private val permissionManager: PermissionManager,
    private val getSettings: GetSettings,
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    private var loadedSettings = NotificationSettings.stub()

    val uiState = settingsStateFactory.createNotificationSettingsState().setup()

    init {
        observeSettings()
        loadSettings()
        loadEmailAddress()
    }

    private fun observeSettings() {
        getNotificationSettings.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { settings ->
                settings?.let {
                    loadedSettings = it
                    renderSettings(it)
                }
            }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadSettings() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getNotificationSettings.refresh() },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun loadEmailAddress() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getSettings().profile.email },
            onComplete = { email ->
                uiState.emailEnabled.supportingTextRes = email.takeIf { it.isNotBlank() }?.desc()
            },
            onError = { handleError(it) },
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
                    uploadNotificationToken()
                } else {
                    renderSettings(loadedSettings)
                }
            },
            onError = { uiState.notifications.add(it.notification()) },
        )
    }

    private fun uploadNotificationToken() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { updateNotificationToken() },
            onError = { handleError(it) },
        )
    }

    private fun save(settings: NotificationSettings) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { updateNotificationSettings(settings) },
            onComplete = {
                uiState.notifications.add(PresentationNotification.SuccessHaptic)
                loadedSettings = it
                renderSettings(it)
            },
            onError = { renderSettings(loadedSettings) },
        )
    }

    private fun renderSettings(settings: NotificationSettings) {
        uiState.appointmentEnabled.isChecked = settings.appointmentEnabled
        val shownChannels = settings.channels.filter { isShown(it.channel) }
        val isOnlyEnabledChannel = shownChannels.count { it.enabled } == 1
        NotificationChannel.entries.forEach { channel ->
            val switch = uiState.channelSwitch(channel)
            val shown = shownChannels.firstOrNull { it.channel == channel }
            switch.isVisible = shown != null
            shown?.let {
                switch.isChecked = it.enabled
                switch.isEnabled = !(it.enabled && isOnlyEnabledChannel)
            }
        }
    }

    private fun isShown(channel: NotificationChannel): Boolean {
        return channel != PUSH_NOTIFICATIONS ||
            permissionManager.supports(PermissionType.NOTIFICATIONS)
    }

    private fun NotificationSettingsState.channelSwitch(
        channel: NotificationChannel,
    ): BooleanState {
        return when (channel) {
            EMAIL -> emailEnabled
            PUSH_NOTIFICATIONS -> pushNotificationsEnabled
            TELEGRAM -> telegramEnabled
        }
    }

    private fun NotificationSettingsState.setup(): NotificationSettingsState = apply {
        appBar.title = SettingsRes.strings.settings_notifications_title.desc()
        appBar.onBackClick = weakVMClosure {
            it.uiState.navigation.push(NotificationSettingsDestinations.Back)
        }

        appointmentEnabled.text =
            SettingsRes.strings.settings_notifications_appointment_label.desc()
        appointmentEnabled.supportingTextRes =
            SettingsRes.strings.settings_notifications_appointment_hint.desc()
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
        telegramEnabled.supportingTextRes =
            SettingsRes.strings.settings_notifications_telegram_hint.desc()
        telegramEnabled.onCheckedChange = weakVMClosure { vm, v ->
            vm.onChannelChanged(TELEGRAM, v)
        }
    }
}
