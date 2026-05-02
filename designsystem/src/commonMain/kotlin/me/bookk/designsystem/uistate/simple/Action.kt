package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.desc.StringDesc

class Action(
    val title: StringDesc,
    val onClick: () -> Unit
) {
    constructor(title: StringDesc) : this(title, {})
}