package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.RadioButtonState

class WebRadioButtonState(
    text: StringDesc = "".desc(),
    isEnabled: Boolean = true,
    isSelected: Boolean = false,
    isVisible: Boolean = true,
    onClick: (() -> Unit)? = null,
) : WebViewState(isVisible),
    RadioButtonState {
    override var text: StringDesc by mutableStateOf(text)
    override var isEnabled: Boolean by mutableStateOf(isEnabled)
    override var isSelected: Boolean by mutableStateOf(isSelected)
    override var onClick: (() -> Unit)? by mutableStateOf(onClick)
}
