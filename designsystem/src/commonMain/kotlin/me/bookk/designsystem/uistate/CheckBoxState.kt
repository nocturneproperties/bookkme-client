package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface CheckBoxState : ViewState {
    var text: StringDesc
    var isEnabled: Boolean
    var isChecked: Boolean
    var isValid: Boolean
    var validationState: ValidationState
    var supportingTextRes: StringDesc?
    var onCheckedChange: ((Boolean) -> Unit)?
}