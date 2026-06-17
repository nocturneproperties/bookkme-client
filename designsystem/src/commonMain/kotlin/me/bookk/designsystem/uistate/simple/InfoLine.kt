package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

data class InfoLine(
    val id: String,
    val title: StringDesc,
    val value: StringDesc,
    val onClick: (() -> Unit)? = null
) {
    constructor(title: StringResource, value: String, onClick: (() -> Unit)? = null) : this(
        id = value,
        title = title.desc(),
        value = value.desc(),
        onClick = onClick
    )
}