package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

interface TextFieldState : ViewState {
    var hint: StringDesc
    var label: StringDesc
    var text: String
    var startIcon: ImageResource?
    var supportingTextRes: StringDesc?
    var isError: Boolean
    var isValid: Boolean
    var enabled: Boolean
    var readOnly: Boolean
    var maxLength: Int
}