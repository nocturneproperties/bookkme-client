package library.money.api

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class CurrencyJsTest {

    @Test
    fun `formats minor units as major units with the locale decimal separator`() = runUnitTest {
        given()
        val currency = Currency("EUR")

        whenn()
        val formatted = currency.format(12345)

        then()
        assertTrue(formatted.contains("123${currency.decimalSeparator()}45"), formatted)
    }

    @Test
    fun `exposes the ISO code`() = runUnitTest {
        given()
        val currency = Currency("PLN")

        whenn()
        val code = currency.code()

        then()
        assertEquals("PLN", code)
    }

    @Test
    fun `describes itself with code and symbol`() = runUnitTest {
        given()
        val currency = Currency("USD")

        whenn()
        val description = currency.asString()

        then()
        assertEquals("USD (${currency.symbol()})", description)
        assertTrue(currency.symbol().isNotBlank())
    }

    @Test
    fun `is equal to a currency with the same code`() = runUnitTest {
        given()
        val currency = Currency("UAH")

        whenn()
        val other = Currency("UAH")

        then()
        assertEquals(currency, other)
        assertEquals(currency.hashCode(), other.hashCode())
        assertNotEquals(currency, Currency("EUR"))
    }
}
