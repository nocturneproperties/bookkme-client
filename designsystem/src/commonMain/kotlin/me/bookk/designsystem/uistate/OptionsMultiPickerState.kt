package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface OptionsMultiPickerState<T : PickerPresentation> : ViewState {

    var pickerTitle: StringDesc
    val options: List<T>
    val selectedItems: List<T>

    var onItemsPicked: (List<T>) -> Unit
    var onItemsRemoveRequested: (List<T>) -> Unit
    var addItemText: StringDesc
    var isEditable: Boolean

    fun replaceOptions(options: List<T>)
    fun replaceSelected(selected: List<T>)
}