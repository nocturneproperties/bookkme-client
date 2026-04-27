package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Immutable
class AndroidButtonState(
    text: StringDesc = "".desc(),
    isLoading: Boolean = false,
    isEnabled: Boolean = true,
    isVisible: Boolean = true,
    onClick: (() -> Unit)? = null,
    icon: ImageResource? = null,
) : AndroidViewState(isVisible), ButtonState {
    override var icon: ImageResource? by mutableStateOf(icon)
    override var text: StringDesc by mutableStateOf(text)
    override var isLoading: Boolean by mutableStateOf(isLoading)
    override var isEnabled: Boolean by mutableStateOf(isEnabled)
    override var onClick: (() -> Unit)? by mutableStateOf(onClick)
}