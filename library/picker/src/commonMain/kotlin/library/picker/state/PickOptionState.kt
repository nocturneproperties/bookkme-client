package library.picker.state

import library.picker.PickerNavigationDestination
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.TextFieldState

interface PickOptionState {
    val appBar: AppBarState
    val queryField: TextFieldState
    val filteredOptions: ListState<PickOptionItem>
    val selectButton: ButtonState

    val navigation: NavigationState<PickerNavigationDestination>
}