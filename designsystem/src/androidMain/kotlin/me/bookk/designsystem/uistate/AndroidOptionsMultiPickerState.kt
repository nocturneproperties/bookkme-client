package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class AndroidOptionsMultiPickerState<T : PickerPresentation> :
    AndroidViewState(),
    OptionsMultiPickerState<T> {
    override var pickerTitle: StringDesc by mutableStateOf("".desc())
    override var pickerType: PickerFieldState.PickerType by mutableStateOf(
        PickerFieldState.PickerType.BOTTOM_SHEET,
    )
    override var placeholder: StringDesc? by mutableStateOf(null)
    override val options = mutableStateListOf<T>()
    override val selectedItems = mutableStateListOf<PickedItem<T>>()
    override var onItemsPicked: (List<T>) -> Unit by mutableStateOf({})
    override var onItemsRemoveRequested: (List<T>) -> Unit by mutableStateOf({})
    override var onQuantityIncreaseRequested: (T) -> Unit by mutableStateOf({})
    override var onQuantityDecreaseRequested: (T) -> Unit by mutableStateOf({})
    override var addItemText: StringDesc by mutableStateOf("".desc())
    override var isEditable: Boolean by mutableStateOf(true)
    override var isQuantityEditable: Boolean by mutableStateOf(false)

    override fun createPickedItem(item: T, quantity: Int): PickedItem<T> {
        return AndroidPickedItem(item, quantity)
    }

    override fun replaceOptions(options: List<T>) {
        this.options.clear()
        this.options.addAll(options)
    }

    override fun replaceSelected(selected: List<PickedItem<T>>) {
        this.selectedItems.clear()
        this.selectedItems.addAll(selected)
    }
}

private class AndroidPickedItem<T : PickerPresentation>(
    override val item: T,
    quantity: Int,
) : PickedItem<T> {
    override var quantity: Int by mutableIntStateOf(quantity)
}
