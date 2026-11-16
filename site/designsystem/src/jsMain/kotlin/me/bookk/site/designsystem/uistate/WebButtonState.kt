package me.bookk.site.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.ButtonState

class WebButtonState(
    text: StringDesc = "".desc(),
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    isVisible: Boolean = true,
    onClick: (() -> Unit)? = null,
    icon: ImageResource? = null,
) : WebViewState(isVisible),
    ButtonState {
    override var icon: ImageResource? by mutableStateOf(icon)
    override var text: StringDesc by mutableStateOf(text)
    override var isLoading: Boolean by mutableStateOf(isLoading)
    override var isEnabled: Boolean by mutableStateOf(isEnabled)
    override var onClick: (() -> Unit)? by mutableStateOf(onClick)
}
