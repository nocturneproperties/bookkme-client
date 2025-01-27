package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface ButtonState : ViewState {
    var text: StringDesc
    var isLoading: Boolean
    var isEnabled: Boolean
}