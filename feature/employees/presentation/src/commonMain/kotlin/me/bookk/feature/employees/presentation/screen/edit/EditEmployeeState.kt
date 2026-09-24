package me.bookk.feature.employees.presentation.screen.edit

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.ViewState
import me.bookk.designsystem.uistate.schedule.ScheduleState
import me.bookk.designsystem.uistate.simple.InfoLine

interface EditEmployeeState {
    val appBar: AppBarState
    val save: ButtonState
    val contacts: ListState<InfoLine>
    val services: OptionsMultiPickerState<EmployeeServicePresentation>
    val schedule: ScheduleState
    val permissions: ListState<ResourcePermissionState>

    val notifications: PresentationNotificationState
    val navigation: NavigationState<EditEmployeeDestinations>
}

interface ResourcePermissionState : ViewState {
    var title: StringDesc
    val canView: BooleanState
    val canUpdate: BooleanState
    val canDelete: BooleanState
}
