package library.money.api

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyTest {

    @BeforeTest
    fun before() {
        startKoin {
            modules(module { single { mockFactory } })
        }
    }

    @AfterTest
    fun after() {
        stopKoin()
    }

    @Test
    fun longConstructor() {
        val money = Money(10000, Money.SupportedCurrency.EUR)

        assertEquals(10000, money.value)
        assertEquals(Money.SupportedCurrency.EUR, money.currency)
    }

    @Test
    fun doubleConstructor() {
        val money = Money(125.84, Money.SupportedCurrency.UAH)

        assertEquals(12584, money.value)
        assertEquals(Money.SupportedCurrency.UAH, money.currency)
    }

    @Test
    fun floatConstructor() {
        val money = Money(12312321.99, Money.SupportedCurrency.UAH)

        assertEquals(1231232199, money.value)
        assertEquals(Money.SupportedCurrency.UAH, money.currency)
    }

    @Test
    fun timesDouble() {
        val money = Money(12322.75, Money.SupportedCurrency.UAH)
        val expected = Money(1544656.71, Money.SupportedCurrency.UAH)
        val result = money * 125.35

        assertEquals(expected, result)
    }

    @Test
    fun timesFloat() {
        val money = Money(12322.75, Money.SupportedCurrency.UAH)
        val expected = Money(928272.75, Money.SupportedCurrency.UAH)
        val result = money * 75.33f

        assertEquals(expected, result)
    }

    @Test
    fun divDouble() {
        val money = Money(12322.75, Money.SupportedCurrency.UAH)
        val expected = Money(163.58, Money.SupportedCurrency.UAH)
        val result = money / 75.33

        assertEquals(expected, result)
    }

    @Test
    fun divFloat() {
        val money = Money(12322.75, Money.SupportedCurrency.UAH)
        val expected = Money(163.58, Money.SupportedCurrency.UAH)
        val result = money / 75.33f

        assertEquals(expected, result)
    }
}