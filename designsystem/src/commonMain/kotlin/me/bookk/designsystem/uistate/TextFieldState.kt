package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface TextFieldState : UiState {
    var hint: StringDesc
    var text: String
    var errorTextRes: StringDesc?
    var isError: Boolean
    var isValid: Boolean
    var enabled: Boolean
    var readOnly: Boolean
    var maxLength: Int
}