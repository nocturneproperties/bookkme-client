package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface TextState : UiState {
    var text: StringDesc
    var isVisible: Boolean
    var isHighlighted: Boolean
}

