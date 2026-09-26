package me.bookk.feature.employees.presentation.screen.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidOptionsMultiPickerState
import me.bookk.designsystem.uistate.AndroidViewState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.schedule.AndroidScheduleState
import me.bookk.designsystem.uistate.schedule.ScheduleState
import me.bookk.designsystem.uistate.simple.InfoLine

internal class AndroidEditEmployeeState : EditEmployeeState {
    override val appBar: AppBarState = AndroidAppBarState()
    override val save: ButtonState = AndroidButtonState()
    override val contacts: ListState<InfoLine> = AndroidListState()
    override val services: OptionsMultiPickerState<EmployeeServicePresentation> = AndroidOptionsMultiPickerState()
    override val schedule: ScheduleState = AndroidScheduleState()
    override val suspension: ButtonState = AndroidButtonState(isVisible = false)
    override val permissions: ListState<ResourcePermissionState> = AndroidListState()
    override var isPermissionsVisible: Boolean by mutableStateOf(false)
    override var permissionsHint: StringDesc? by mutableStateOf(null)
    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<EditEmployeeDestinations> = AndroidNavigationState()
}

internal class AndroidResourcePermissionState : AndroidViewState(), ResourcePermissionState {
    override var title: StringDesc by mutableStateOf("".desc())
    override val canView: BooleanState = AndroidBooleanState()
    override val canUpdate: BooleanState = AndroidBooleanState()
    override val canDelete: BooleanState = AndroidBooleanState()
}
