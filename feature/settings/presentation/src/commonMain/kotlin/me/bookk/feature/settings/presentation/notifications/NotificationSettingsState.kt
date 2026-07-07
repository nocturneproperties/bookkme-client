package me.bookk.feature.settings.presentation.notifications

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface NotificationSettingsState {
    val appBar: AppBarState
    val appointmentEnabled: BooleanState
    val emailEnabled: BooleanState
    val pushNotificationsEnabled: BooleanState
    val telegramEnabled: BooleanState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<NotificationSettingsDestinations>
}
