package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.DesignSystem

class TextImageAction(
    val image: ImageResource,
    val title: StringDesc,
    val onClick: () -> Unit
) {
    constructor(image: ImageResource, title: StringDesc) : this(image, title, {})
    constructor() : this(DesignSystem.images.error, "Undefined".desc())
}