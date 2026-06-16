package me.bookk.feature.business.presentation.screen.plugins

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.simple.Action
import me.bookk.designsystem.uistate.simple.OptionalInfoLine

interface BusinessPluginListState {
    val appBar: AppBarState

    val appointmentPlugin: BusinessPluginState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<BusinessPluginsDestinations>
}

interface BusinessPluginState {
    var title: StringDesc
    var subtitle: StringDesc
    var isEnabled: Boolean
    var isExpanded: Boolean
    val youCan: ListState<OptionalInfoLine>
    val clientCan: ListState<OptionalInfoLine>
    var demo: Action?
    val enable: ButtonState
}