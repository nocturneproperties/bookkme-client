package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface TextState : ViewState {
    var text: StringDesc
    var isHighlighted: Boolean
}

