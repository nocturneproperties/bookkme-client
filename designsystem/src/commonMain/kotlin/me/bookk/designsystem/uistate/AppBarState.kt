package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface AppBarState : UiState {
    var title: StringDesc
    var subtitle: StringDesc?
}
