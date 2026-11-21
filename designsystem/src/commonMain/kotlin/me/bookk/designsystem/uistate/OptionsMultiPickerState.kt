package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface OptionsMultiPickerState<T : PickerPresentation> : ViewState {

    var pickerTitle: StringDesc
    var pickerType: PickerFieldState.PickerType
    var placeholder: StringDesc?
    val options: List<T>
    val selectedItems: List<PickedItem<T>>

    var onItemsPicked: (List<T>) -> Unit
    var onItemsRemoveRequested: (List<T>) -> Unit
    var onQuantityIncreaseRequested: (T) -> Unit
    var onQuantityDecreaseRequested: (T) -> Unit
    var addItemText: StringDesc
    var isEditable: Boolean
    var isQuantityEditable: Boolean

    fun createPickedItem(item: T, quantity: Int): PickedItem<T>
    fun replaceOptions(options: List<T>)
    fun replaceSelected(selected: List<PickedItem<T>>)
}
