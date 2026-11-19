package me.bookk.site.landing.business

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class TimeSlotTest {

    @Test
    fun `labels a time as hours and minutes`() = runUnitTest {
        given()
        val slot = TimeSlot(15, 30, isSelected = false)

        whenn()
        val label = slot.label()

        then()
        assertEquals("15:30", label)
    }

    @Test
    fun `pads single digit hours and minutes with a zero`() = runUnitTest {
        given()
        val slot = TimeSlot(9, 5, isSelected = false)

        whenn()
        val label = slot.label()

        then()
        assertEquals("09:05", label)
    }
}
