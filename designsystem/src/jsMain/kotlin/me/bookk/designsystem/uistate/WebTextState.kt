package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.TextState

class WebTextState(
    text: StringDesc = "".desc(),
    isVisible: Boolean = true,
    isHighlighted: Boolean = false,
) : WebViewState(isVisible),
    TextState {
    override var text: StringDesc by mutableStateOf(text)
    override var isHighlighted: Boolean by mutableStateOf(isHighlighted)
}
