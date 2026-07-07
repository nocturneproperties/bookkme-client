package me.bookk.feature.settings.presentation.notifications

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidNotificationSettingsState : NotificationSettingsState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val appointmentEnabled: BooleanState = AndroidBooleanState()
    override val emailEnabled: BooleanState = AndroidBooleanState()
    override val pushNotificationsEnabled: BooleanState = AndroidBooleanState()
    override val telegramEnabled: BooleanState = AndroidBooleanState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<NotificationSettingsDestinations> = AndroidNavigationState()
}
