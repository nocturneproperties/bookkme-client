package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.desc.StringDesc

data class InfoLine(
    val title: StringDesc,
    val value: StringDesc,
    val onClick: (() -> Unit)? = null
)