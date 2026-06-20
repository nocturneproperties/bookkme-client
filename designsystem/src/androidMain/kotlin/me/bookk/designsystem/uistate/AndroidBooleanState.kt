package me.bookk.designsystem.uistate

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

@Immutable
class AndroidBooleanState(
    isVisible: Boolean = true,
    isChecked: Boolean = false,
    validationState: ValidationState = ValidationState.DEFAULT,
    isEnabled: Boolean = true,
    isValid: Boolean = true,
    text: StringDesc = "".desc(),
    onCheckedChange: ((Boolean) -> Unit)? = null
) : AndroidViewState(isVisible), BooleanState {
    override var isEnabled: Boolean by mutableStateOf(isEnabled)
    override var isChecked: Boolean by mutableStateOf(isChecked)
    override var text: StringDesc by mutableStateOf(text)
    override var validationState: ValidationState by mutableStateOf(validationState)
    override var isValid: Boolean by mutableStateOf(isValid)
    override var supportingTextRes: StringDesc? by mutableStateOf(null)
    override var onCheckedChange: ((Boolean) -> Unit)? by mutableStateOf(onCheckedChange)
}