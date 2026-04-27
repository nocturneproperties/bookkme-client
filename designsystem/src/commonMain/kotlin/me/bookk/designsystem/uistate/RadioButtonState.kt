package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface RadioButtonState : ViewState {
    var text: StringDesc
    var isEnabled: Boolean
    var isSelected: Boolean
    var onClick: (() -> Unit)?
}