package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.ImageResource

class ImageAction(
    val image: ImageResource,
    val onClick: () -> Unit
)