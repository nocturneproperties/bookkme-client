package me.bookk.designsystem.uistate

import dev.icerock.moko.resources.desc.StringDesc

interface AppBarState : ViewState {
    var title: StringDesc
    var subtitle: StringDesc?
}
