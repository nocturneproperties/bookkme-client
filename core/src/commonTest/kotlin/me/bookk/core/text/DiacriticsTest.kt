package me.bookk.core.text

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class DiacriticsTest {

    @Test
    fun `removes accents from latin letters`() = runUnitTest {
        given()
        val text = "Zoë Núñez Gonçalves"

        whenn()
        val result = text.withoutDiacritics()

        then()
        assertEquals("Zoe Nunez Goncalves", result)
    }

    @Test
    fun `removes combining marks from cyrillic letters`() = runUnitTest {
        given()
        val text = "Йосип Їжак"

        whenn()
        val result = text.withoutDiacritics()

        then()
        assertEquals("Иосип Іжак", result)
    }

    @Test
    fun `keeps letters that have no decomposition`() = runUnitTest {
        given()
        val text = "Łukasz Øster"

        whenn()
        val result = text.withoutDiacritics()

        then()
        assertEquals("Łukasz Øster", result)
    }
}
