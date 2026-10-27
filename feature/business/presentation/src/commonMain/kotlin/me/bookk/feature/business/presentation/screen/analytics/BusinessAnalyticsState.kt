package me.bookk.feature.business.presentation.screen.analytics

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface BusinessAnalyticsState {
    val appBar: AppBarState
    var title: StringDesc
    var message: StringDesc
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BusinessAnalyticsDestinations>
}
