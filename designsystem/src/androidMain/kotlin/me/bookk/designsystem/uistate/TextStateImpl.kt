package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Immutable
class TextStateImpl(
    text: StringDesc = "".desc(),
    isVisible: Boolean = false,
    isHighlighted: Boolean = false,
) : TextState {
    override var text: StringDesc by mutableStateOf(text)
    override var isVisible: Boolean by mutableStateOf(isVisible)
    override var isHighlighted: Boolean by mutableStateOf(isHighlighted)
}