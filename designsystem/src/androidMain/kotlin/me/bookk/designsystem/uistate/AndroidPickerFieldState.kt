package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Immutable
class AndroidPickerFieldState<T>(
    text: String = "",
    selectedItem: T,
    items: List<T>
) : PickerFieldState<T> {
    override var text: String by mutableStateOf(text)
    override var options: MutableList<T> = mutableStateListOf<T>().apply {
        addAll(items)
    }
    override var selectedItem: T by mutableStateOf(selectedItem)

}