package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface BooleanState : ViewState {
    var text: StringDesc
    var isEnabled: Boolean
    var isChecked: Boolean
    var isValid: Boolean
    var validationState: ValidationState
    var supportingTextRes: StringDesc?
    var onCheckedChange: ((Boolean) -> Unit)?
}

fun BooleanState.clearError() {
    supportingTextRes = null
    isValid = true
    validationState = ValidationState.DEFAULT
}

fun BooleanState.showError(error: StringDesc) {
    supportingTextRes = error
    isValid = false
    validationState = ValidationState.ERROR
}

fun BooleanState.validate(error: StringDesc?): Boolean {
    if (error == null) clearError() else showError(error)
    return error == null
}
