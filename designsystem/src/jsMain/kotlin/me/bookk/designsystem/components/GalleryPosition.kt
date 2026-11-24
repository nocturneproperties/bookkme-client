package me.bookk.designsystem.components

import kotlin.math.roundToInt

private const val EDGE_TOLERANCE = 4.0

data class GalleryMetrics(
    val scrollLeft: Double,
    val viewportWidth: Double,
    val scrollWidth: Double,
    val itemStep: Double,
    val itemCount: Int,
)

data class GalleryPosition(
    val index: Int,
    val canGoBack: Boolean,
    val canGoForward: Boolean,
) {
    companion object {
        val START = GalleryPosition(index = 0, canGoBack = false, canGoForward = false)

        fun of(metrics: GalleryMetrics): GalleryPosition {
            val isAtEnd =
                metrics.scrollLeft >= metrics.scrollWidth - metrics.viewportWidth - EDGE_TOLERANCE
            return GalleryPosition(
                index = metrics.index(isAtEnd),
                canGoBack = metrics.scrollLeft > EDGE_TOLERANCE,
                canGoForward = !isAtEnd,
            )
        }

        private fun GalleryMetrics.index(isAtEnd: Boolean): Int {
            return when {
                itemStep <= 0.0 -> 0
                isAtEnd -> itemCount - 1
                else -> (scrollLeft / itemStep).roundToInt().coerceIn(0, itemCount - 1)
            }
        }
    }
}
