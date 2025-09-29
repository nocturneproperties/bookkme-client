package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Immutable
class AndroidPickerFieldState<T : PickerPresentation>(
    text: StringDesc = "".desc(),
    selectedItem: T,
    items: List<T>
) : PickerFieldState<T> {
    override var text: StringDesc by mutableStateOf(text)
    override val options: MutableList<T> = mutableStateListOf<T>().apply {
        addAll(items)
    }

    override var selectedItem: T by mutableStateOf(selectedItem)

    override fun replaceOptions(options: List<T>) {
        this.options.clear()
        this.options.addAll(options)
    }

}