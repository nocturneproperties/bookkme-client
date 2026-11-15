package me.bookk.core.presentation.date

import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.datetime.LocalDate
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JsStartOfWeekTest {

    @AfterTest
    fun tearDown() {
        StringDesc.localeType = StringDesc.LocaleType.System
    }

    @Test
    fun `starts the week on sunday for a us locale`() = runUnitTest {
        given()
        StringDesc.localeType = StringDesc.LocaleType.Custom("en-US")

        whenn()
        val start = LocalDate(2026, 11, 18).startOfWeek()

        then()
        assertEquals(LocalDate(2026, 11, 15), start)
    }

    @Test
    fun `starts the week on monday for a polish locale`() = runUnitTest {
        given()
        StringDesc.localeType = StringDesc.LocaleType.Custom("pl")

        whenn()
        val start = LocalDate(2026, 11, 18).startOfWeek()

        then()
        assertEquals(LocalDate(2026, 11, 16), start)
    }

    @Test
    fun `keeps the date when it already is the first day of the week`() = runUnitTest {
        given()
        StringDesc.localeType = StringDesc.LocaleType.Custom("pl")

        whenn()
        val start = LocalDate(2026, 11, 16).startOfWeek()

        then()
        assertEquals(LocalDate(2026, 11, 16), start)
    }
}
