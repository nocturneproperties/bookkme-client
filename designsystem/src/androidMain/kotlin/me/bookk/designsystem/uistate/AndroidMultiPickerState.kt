package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class AndroidMultiPickerState<T : PickerPresentation> : AndroidViewState(), MultiPickerState<T> {
    override var pickerTitle: StringDesc by mutableStateOf("".desc())
    override val options = mutableStateListOf<T>()
    override val selectedItems = mutableStateListOf<T>()
    override var onItemsPicked: (List<T>) -> Unit by mutableStateOf({})
    override var onItemsRemoveRequested: (List<T>) -> Unit by mutableStateOf({})
    override var addItemText: StringDesc by mutableStateOf("".desc())
    override var isEditable: Boolean by mutableStateOf(true)

    override fun replaceOptions(options: List<T>) {
        this.options.clear()
        this.options.addAll(options)
    }

    override fun replaceSelected(selected: List<T>) {
        this.selectedItems.clear()
        this.selectedItems.addAll(selected)
    }
}