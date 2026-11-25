package me.bookk.feature.settings.presentation.notifications

import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebBooleanState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState

internal class WebNotificationSettingsState : NotificationSettingsState {
    override val appBar = WebAppBarState()
    override val appointmentEnabled = WebBooleanState()
    override val emailEnabled = WebBooleanState()
    override val pushNotificationsEnabled = WebBooleanState()
    override val telegramEnabled = WebBooleanState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<NotificationSettingsDestinations>()
}
