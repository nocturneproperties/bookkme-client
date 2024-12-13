package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface OptionState : UiState {
    val id: String
    var title: StringDesc
    var isSelected: Boolean
}