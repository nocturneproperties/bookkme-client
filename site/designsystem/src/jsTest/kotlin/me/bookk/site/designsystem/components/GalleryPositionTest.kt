package me.bookk.site.designsystem.components

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class GalleryPositionTest {

    @Test
    fun `starts on the first item with only the next arrow enabled`() = runUnitTest {
        given()
        val metrics = metricsAt(scrollLeft = 0.0)

        whenn()
        val position = GalleryPosition.of(metrics)

        then()
        assertEquals(GalleryPosition(index = 0, canGoBack = false, canGoForward = true), position)
    }

    @Test
    fun `selects the item nearest to the scroll position`() = runUnitTest {
        given()
        val metrics = metricsAt(scrollLeft = 520.0)

        whenn()
        val position = GalleryPosition.of(metrics)

        then()
        assertEquals(GalleryPosition(index = 1, canGoBack = true, canGoForward = true), position)
    }

    @Test
    fun `selects the last item once the track is scrolled to its end`() = runUnitTest {
        given()
        val metrics = metricsAt(scrollLeft = 800.0)

        whenn()
        val position = GalleryPosition.of(metrics)

        then()
        assertEquals(GalleryPosition(index = 2, canGoBack = true, canGoForward = false), position)
    }

    @Test
    fun `treats a position a few pixels short of the end as the end`() = runUnitTest {
        given()
        val metrics = metricsAt(scrollLeft = 797.5)

        whenn()
        val position = GalleryPosition.of(metrics)

        then()
        assertEquals(GalleryPosition(index = 2, canGoBack = true, canGoForward = false), position)
    }

    @Test
    fun `stays on the first item while the track has no width`() = runUnitTest {
        given()
        val metrics = GalleryMetrics(
            scrollLeft = 0.0,
            viewportWidth = 0.0,
            scrollWidth = 0.0,
            itemStep = 0.0,
            itemCount = 3,
        )

        whenn()
        val position = GalleryPosition.of(metrics)

        then()
        assertEquals(GalleryPosition(index = 0, canGoBack = false, canGoForward = false), position)
    }

    private fun metricsAt(scrollLeft: Double): GalleryMetrics {
        return GalleryMetrics(
            scrollLeft = scrollLeft,
            viewportWidth = 900.0,
            scrollWidth = 1700.0,
            itemStep = 600.0,
            itemCount = 3,
        )
    }
}
