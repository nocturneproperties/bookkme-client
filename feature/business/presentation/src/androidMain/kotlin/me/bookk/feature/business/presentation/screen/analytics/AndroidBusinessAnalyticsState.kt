package me.bookk.feature.business.presentation.screen.analytics

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

internal class AndroidBusinessAnalyticsState : BusinessAnalyticsState {
    override val appBar: AppBarState = AndroidAppBarState()
    override var title: StringDesc by mutableStateOf("".desc())
    override var message: StringDesc by mutableStateOf("".desc())

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<BusinessAnalyticsDestinations> = AndroidNavigationState()
}
