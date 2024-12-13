package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Immutable
class ButtonStateImpl(
    text: StringDesc = "".desc(),
    isLoading: Boolean = false,
    isEnabled: Boolean = true
) : ButtonState {
    override var text: StringDesc by mutableStateOf(text)
    override var isLoading: Boolean by mutableStateOf(isLoading)
    override var isEnabled: Boolean by mutableStateOf(isEnabled)
}