package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.desc.StringDesc
import kotlin.uuid.Uuid

data class InfoLine(
    val title: StringDesc,
    val value: StringDesc,
    val onClick: (() -> Unit)? = null
) {
    val id = Uuid.random().toString()
}