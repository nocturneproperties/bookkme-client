package library.picker.state

import dev.icerock.moko.resources.desc.desc
import library.picker.PickerNavigationDestination
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidListState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.TextFieldState

class AndroidPickOptionState : PickOptionState {
    override val appBar: AppBarState = AndroidAppBarState("".desc())
    override val queryField: TextFieldState = AndroidTextFieldState()
    override val filteredOptions = AndroidListState<PickOptionItem>()
    override val selectButton: ButtonState = AndroidButtonState()
    override val navigation = AndroidNavigationState<PickerNavigationDestination>()
}