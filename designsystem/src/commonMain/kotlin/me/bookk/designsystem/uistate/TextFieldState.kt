package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

interface TextFieldState : ViewState {
    var placeholder: StringDesc
    var label: StringDesc
    var text: String
    var suffix: StringDesc?
    var startIcon: ImageResource?
    var endIcon: ImageResource?
    var supportingTextRes: StringDesc?
    var validationState: ValidationState
    var inputType: InputType
    var isValid: Boolean
    var enabled: Boolean
    var readOnly: Boolean
    var maxLength: Int
    var onTextChanged: ((String) -> Unit)?

    fun updateText(desc: StringDesc?)
}

enum class InputType {
    TEXT,
    DIGIT,
    DECIMAL,
    PHONE,
    EMAIL,
    ASCII,
    PASSWORD
}

fun TextFieldState.clearError() {
    supportingTextRes = null
    isValid = true
    validationState = ValidationState.DEFAULT
}

fun TextFieldState.showError(error: StringDesc) {
    supportingTextRes = error
    isValid = false
    validationState = ValidationState.ERROR
}