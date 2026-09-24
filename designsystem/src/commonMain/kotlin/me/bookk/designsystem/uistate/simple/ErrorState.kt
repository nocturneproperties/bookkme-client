package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.desc.StringDesc

class ErrorState(
    val title: StringDesc,
    val subtitle: StringDesc,
    val onRetryClick: () -> Unit
)
