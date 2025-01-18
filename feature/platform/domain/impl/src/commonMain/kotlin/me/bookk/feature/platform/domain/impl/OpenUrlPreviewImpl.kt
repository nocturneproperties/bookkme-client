package me.bookk.feature.platform.domain.impl

import me.bookk.feature.platform.domain.api.OpenUrlPreview
import me.bookk.feature.platform.domain.datasource.PlatformInterface

class OpenUrlPreviewImpl(
    private val platformInterface: PlatformInterface
) : OpenUrlPreview {
    override fun invoke(url: String) {
        platformInterface.openUrlPreview(url)
    }
}