package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.PickerFieldState.PickerType

@Immutable
class AndroidPickerFieldState<T : PickerPresentation>(
    selectedItem: T? = null,
    title: StringDesc = "".desc(),
    type: PickerType = PickerType.BOTTOM_SHEET,
    items: List<T> = emptyList()
) : AndroidViewState(isVisible = true), PickerFieldState<T> {
    override val textField: TextFieldState = AndroidTextFieldState(readOnly = true)
    override var pickerTitle: StringDesc by mutableStateOf(title)
    override var pickerType: PickerType by mutableStateOf(type)
    override val options: MutableList<T> = mutableStateListOf<T>().apply {
        addAll(items)
    }

    override var selectedItem: T? by mutableStateOf(selectedItem)
    override var onItemPicked: (T?) -> Unit = {}

    override fun replaceOptions(options: List<T>) {
        this.options.clear()
        this.options.addAll(options)
    }

}