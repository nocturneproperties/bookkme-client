package me.bookk.designsystem.uistate.simple

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.DesignSystem

class BannerErrorState(
    val text: StringDesc,
    val onRetryClick: () -> Unit
) {
    companion object {
        fun default(onRetryClick: () -> Unit): BannerErrorState = BannerErrorState(
            text = DesignSystem.strings.error_refresh_failed.desc(),
            onRetryClick = onRetryClick
        )
    }
}
