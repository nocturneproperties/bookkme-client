package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Immutable
class TextFieldStateImpl (
    hint: StringDesc = "".desc(),
    text: String = "",
    errorTextRes: StringDesc? = null,
    isError: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
) : TextFieldState {
    override var hint: StringDesc by mutableStateOf(hint)
    override var text: String by mutableStateOf(text)
    override var errorTextRes: StringDesc? by mutableStateOf(errorTextRes)
    override var isError: Boolean by mutableStateOf(isError)
    override var enabled: Boolean by mutableStateOf(enabled)
    override var readOnly: Boolean by mutableStateOf(readOnly)
    override var maxLength: Int by mutableIntStateOf(maxLength)
}