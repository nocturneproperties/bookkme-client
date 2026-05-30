package library.money.api

import kotlinx.serialization.Serializable
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Class created to avoid floating point operations and gain predictable money calculations.
 * Value represented as long in cents.
 *
 * 12525 = 125.25
 * 23000 = 230.00
 * 123123212 = 1231232.12
 * */

@Serializable
data class Money(
    val value: Long,
    val currencyType: SupportedCurrency,
) {

    val currency: Currency = CurrencyFactory.forCode(currencyType.code)

    constructor(
        value: Double,
        currency: SupportedCurrency
    ) : this(value.asPreciseLong(), currency)

    constructor(
        value: Float,
        currency: SupportedCurrency
    ) : this(value.asPreciseLong(), currency)

    operator fun times(times: Int): Money {
        return Money(value * times, currencyType)
    }

    operator fun times(times: Float): Money {
        val multiplier = times.asPreciseLong()
        val result = value * multiplier / PRECISION_MULTIPLIER
        return Money(result, currencyType)
    }

    operator fun times(times: Double): Money {
        val multiplier = times.asPreciseLong()
        val result = value * multiplier / PRECISION_MULTIPLIER
        return Money(result, currencyType)
    }

    operator fun div(div: Int): Money {
        return Money(value / div, currencyType)
    }

    operator fun div(div: Float): Money {
        val divider = div.asPreciseLong()
        val result = floor(value / divider.toDouble() * PRECISION_MULTIPLIER).toLong()
        return Money(result, currencyType)
    }

    operator fun div(div: Double): Money {
        val divider = div.asPreciseLong()
        val result = floor(value / divider.toDouble() * PRECISION_MULTIPLIER).toLong()
        return Money(result, currencyType)
    }

    override fun toString(): String {
        return currency.format(value)
    }

    fun valueToString(): String {
        return currency.format(value)
            .replace(currency.symbol(), "")
            .trim()
    }

    fun valueToStringWithoutAmountSeparation(): String {
        val separators = ",."
        return valueToString()
            .filter { it.isDigit() || it in separators }
            .replace(',', '.')
    }

    enum class SupportedCurrency(val code: String) {
        UAH("UAH"),
        USD("USD"),
        EUR("EUR"),
        PLN("PLN"),
        CZK("CZK"),
        GBP("GBP");

        companion object {
            fun fromCode(code: String): SupportedCurrency {
                return SupportedCurrency.entries.first { it.code == code }
            }
        }
    }

    companion object {
        private const val PRECISION = 2
        private val PRECISION_MULTIPLIER = 10.0.pow(PRECISION).roundToLong()

        private fun Float.asPreciseLong(): Long {
            return (this * PRECISION_MULTIPLIER).roundToLong()
        }

        private fun Double.asPreciseLong(): Long {
            return (this * PRECISION_MULTIPLIER).roundToLong()
        }
    }
}