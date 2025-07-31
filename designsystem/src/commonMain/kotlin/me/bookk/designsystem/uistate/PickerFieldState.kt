package me.bookk.designsystem.uistate

interface PickerFieldState<T> {
    var text: String
    var options: MutableList<T>
    var selectedItem: T
}