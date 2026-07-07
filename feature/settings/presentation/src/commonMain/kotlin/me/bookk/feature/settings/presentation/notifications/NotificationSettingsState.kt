package me.bookk.feature.settings.presentation.notifications

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface NotificationSettingsState {
    val appBar: AppBarState
    val notifications: PresentationNotificationState
}
