package me.bookk.designsystem.uistate

import android.content.Context
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import org.koin.mp.KoinPlatform

@Immutable
class AndroidTextFieldState(
    placeholder: StringDesc = "".desc(),
    text: String = "",
    label: StringDesc = "".desc(),
    startIcon: ImageResource? = null,
    endIcon: ImageResource? = null,
    supportingTextRes: StringDesc? = null,
    validationState: ValidationState = ValidationState.DEFAULT,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    maxLength: Int = Int.MAX_VALUE,
    isValid: Boolean = false,
    isVisible: Boolean = true,
    inputType: InputType = InputType.TEXT,
    onTextChanged: ((String) -> Unit)? = null
) : AndroidViewState(isVisible), TextFieldState {
    override var placeholder: StringDesc by mutableStateOf(placeholder)
    override var label: StringDesc by mutableStateOf(label)
    override var text: String by mutableStateOf(text)
    override var startIcon: ImageResource? by mutableStateOf(startIcon)
    override var endIcon: ImageResource? by mutableStateOf(endIcon)
    override var supportingTextRes: StringDesc? by mutableStateOf(supportingTextRes)
    override var validationState: ValidationState by mutableStateOf(validationState)
    override var enabled: Boolean by mutableStateOf(enabled)
    override var readOnly: Boolean by mutableStateOf(readOnly)
    override var maxLength: Int by mutableIntStateOf(maxLength)
    override var isValid: Boolean by mutableStateOf(isValid)
    override var onTextChanged: ((String) -> Unit)? by mutableStateOf(onTextChanged)
    override var inputType: InputType by mutableStateOf(inputType)

    override fun updateText(desc: StringDesc?) {
        text = desc?.toString(KoinPlatform.getKoin().get<Context>()).orEmpty()
    }
}