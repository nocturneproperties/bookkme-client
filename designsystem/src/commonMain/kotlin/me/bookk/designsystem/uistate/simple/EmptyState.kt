package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc

open class EmptyState(
    val image: ImageResource,
    val label: StringDesc,
    val refreshAction: (() -> Unit)? = null
)