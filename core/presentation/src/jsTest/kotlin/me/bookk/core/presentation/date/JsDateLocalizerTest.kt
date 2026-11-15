package me.bookk.core.presentation.date

import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.minus
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JsDateLocalizerTest {

    @AfterTest
    fun tearDown() {
        StringDesc.localeType = StringDesc.LocaleType.System
    }

    private class Fixture {
        val sut = JsDateLocalizer()
    }

    @Test
    fun `formats a date with the day month year skeleton in the chosen locale`() = runUnitTest {
        given()
        StringDesc.localeType = StringDesc.LocaleType.Custom("en-US")
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.forStyle(
            DateStyle.D_MMM_YYYY_RELATIVE,
        ).format(LocalDate(2020, 3, 5))

        then()
        assertEquals("Mar 5, 2020", formatted)
    }

    @Test
    fun `formats a relative date of today as capitalized today`() = runUnitTest {
        given()
        StringDesc.localeType = StringDesc.LocaleType.Custom("en-US")
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.forStyle(
            DateStyle.MEDIUM,
        ).format(LocalDate.today(), relative = true)

        then()
        assertEquals("Today", formatted)
    }

    @Test
    fun `formats a relative date of yesterday in the chosen language`() = runUnitTest {
        given()
        StringDesc.localeType = StringDesc.LocaleType.Custom("uk")
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.forStyle(DateStyle.MEDIUM)
            .format(LocalDate.today().minus(1, DateTimeUnit.DAY), relative = true)

        then()
        assertEquals("Учора", formatted)
    }

    @Test
    fun `formats time with a 24 hour clock for the HH mm skeleton`() = runUnitTest {
        given()
        StringDesc.localeType = StringDesc.LocaleType.Custom("en-US")
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.forStyle(DateStyle.D_MMM_YYYY_RELATIVE).format(LocalTime(14, 5))

        then()
        assertEquals("14:05", formatted)
    }

    @Test
    fun `formats a full weekday name`() = runUnitTest {
        given()
        StringDesc.localeType = StringDesc.LocaleType.Custom("pl")
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.forStyle(DateStyle.FULL_WEEKDAY).format(LocalDate(2026, 11, 16))

        then()
        assertEquals("poniedziałek", formatted)
    }

    @Test
    fun `formats a strict pattern exactly`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.strict(
            "yyyy-MM-dd HH:mm",
        ).format(LocalDateTime(2026, 1, 2, 3, 4))

        then()
        assertEquals("2026-01-02 03:04", formatted)
    }
}
