package me.bookk.feature.settings.presentation.notifications

import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidNotificationSettingsState : NotificationSettingsState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val notifications: PresentationNotificationState = AndroidNotificationState()
}
