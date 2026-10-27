package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

interface ButtonState : ViewState {
    var icon: ImageResource?
    var text: StringDesc
    var isLoading: Boolean
    var isEnabled: Boolean
    var onClick: (() -> Unit)?
}

fun ButtonState.startLoading() {
    isLoading = true
    isEnabled = false
}

fun ButtonState.stopLoading(enable: Boolean = true) {
    isLoading = false
    isEnabled = enable
}