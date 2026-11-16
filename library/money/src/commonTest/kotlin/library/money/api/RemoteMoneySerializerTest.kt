package library.money.api

import kotlinx.serialization.json.Json
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class RemoteMoneySerializerTest {

    private class Fixture {
        val sut = RemoteMoneySerializer()

        fun encode(money: Money): String {
            return Json.encodeToString(sut, money)
        }

        fun decode(wire: String): Money {
            return Json.decodeFromString(sut, "\"$wire\"")
        }
    }

    @Test
    fun `encodes a whole amount with two decimals`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.encode(Money(10000, Money.SupportedCurrency.USD))

        then()
        assertEquals("\"USD 100.00\"", result)
    }

    @Test
    fun `encodes single digit cents with a trailing zero`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.encode(Money(12550, Money.SupportedCurrency.EUR))

        then()
        assertEquals("\"EUR 125.50\"", result)
    }

    @Test
    fun `encodes a large amount without an exponent`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.encode(Money(1_000_000_000, Money.SupportedCurrency.UAH))

        then()
        assertEquals("\"UAH 10000000.00\"", result)
    }

    @Test
    fun `encodes a negative amount below one unit`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.encode(Money(-50, Money.SupportedCurrency.PLN))

        then()
        assertEquals("\"PLN -0.50\"", result)
    }

    @Test
    fun `decodes every cent of a backend amount`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.decode("USD 125.25")

        then()
        assertEquals(Money(12525, Money.SupportedCurrency.USD), result)
    }

    @Test
    fun `decodes an amount without decimals`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.decode("GBP 100")

        then()
        assertEquals(Money(10000, Money.SupportedCurrency.GBP), result)
    }

    @Test
    fun `decodes a negative amount`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.decode("CZK -0.50")

        then()
        assertEquals(Money(-50, Money.SupportedCurrency.CZK), result)
    }
}
