package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface SwitchState : ViewState {
    var text: StringDesc
    var isChecked: Boolean
}