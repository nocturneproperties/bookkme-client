package library.money.api

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CurrencyTest {

    private class Fixture {
        val sut = Currency("USD")
        val separator = sut.decimalSeparator()
    }

    @Test
    fun `drops the fraction of a whole amount`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.format(15000)

        then()
        assertTrue(formatted.contains("150"), formatted)
        assertFalse(formatted.contains("150${fixture.separator}"), formatted)
    }

    @Test
    fun `drops the fraction of a negative whole amount`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.format(-15000)

        then()
        assertTrue(formatted.contains("150"), formatted)
        assertFalse(formatted.contains("150${fixture.separator}"), formatted)
    }

    @Test
    fun `keeps both fraction digits when only the last one is zero`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.format(10050)

        then()
        assertTrue(formatted.contains("100${fixture.separator}50"), formatted)
    }

    @Test
    fun `keeps both fraction digits when only the first one is zero`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val formatted = fixture.sut.format(10005)

        then()
        assertTrue(formatted.contains("100${fixture.separator}05"), formatted)
    }
}
