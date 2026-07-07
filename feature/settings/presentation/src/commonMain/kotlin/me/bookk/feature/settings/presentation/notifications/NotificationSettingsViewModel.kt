package me.bookk.feature.settings.presentation.notifications

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.settings.presentation.SettingsStateFactory

class NotificationSettingsViewModel(
    settingsStateFactory: SettingsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: NotificationSettingsState = settingsStateFactory.createNotificationSettingsState().setup()

    private fun NotificationSettingsState.setup() = apply {
        appBar.title = SettingsRes.strings.settings_notifications_title.desc()
    }
}
