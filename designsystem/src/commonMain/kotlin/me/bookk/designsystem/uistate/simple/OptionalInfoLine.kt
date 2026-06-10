package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.desc.StringDesc
import kotlin.uuid.Uuid

class OptionalInfoLine(
    val title: StringDesc,
    val value: StringDesc? = null,
    val onClick: (() -> Unit)? = null
) {
    val id = Uuid.random().toString()
}

fun StringDesc.optionalLine() = OptionalInfoLine(this)