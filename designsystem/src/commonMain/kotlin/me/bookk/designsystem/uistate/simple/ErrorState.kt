package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.DesignSystem

class ErrorState(
    val errorText: StringDesc,
    val onRetryClick: () -> Unit
) {
    companion object {
        fun default(onRetryClick: () -> Unit): ErrorState = ErrorState(
            errorText = DesignSystem.strings.error_in_list.desc(),
            onRetryClick = onRetryClick
        )
    }
}