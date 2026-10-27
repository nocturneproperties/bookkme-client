package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Immutable
class AndroidRadioButtonState(
    isVisible: Boolean = true,
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    text: StringDesc = "".desc(),
    onCheckedChange: (() -> Unit)? = null
) : AndroidViewState(isVisible), RadioButtonState {
    override var isEnabled: Boolean by mutableStateOf(isEnabled)
    override var isSelected: Boolean by mutableStateOf(isSelected)
    override var onClick: (() -> Unit)? by mutableStateOf(onCheckedChange)
    override var text: StringDesc by mutableStateOf(text)
}
