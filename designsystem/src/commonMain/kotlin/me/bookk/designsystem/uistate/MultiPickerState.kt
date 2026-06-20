package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface MultiPickerState<T: PickerPresentation> : ViewState {
    var pickerTitle: StringDesc
    val selectedItems: List<T>
    var onItemsPicked: (List<T>) -> Unit
    var onItemsRemoveRequested: (List<T>) -> Unit
    var addItemText: StringDesc
    var isEditable: Boolean

    fun replaceSelected(items: List<T>)
}