package library.money.api

import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToLong

/**
 * Class created to avoid floating point operations and gain predictable money calculations.
 * Value represented as long with precision up to 5 decimal places.
 *
 * 12525000 = 125.25
 * 23000000 = 230.00
 * 123123212345 = 1231232.12345
 * */

data class Money(
    val value: Long,
    val currency: SupportedCurrency,
) {

    constructor(
        value: Double,
        currency: SupportedCurrency
    ) : this(value.asPreciseLong(), currency)

    constructor(
        value: Float,
        currency: SupportedCurrency
    ) : this(value.asPreciseLong(), currency)

    private val platformCurrency: Currency = CurrencyFactory.forCode(currency.name)

    operator fun times(times: Int): Money {
        return Money(value * times, currency)
    }

    operator fun times(times: Float): Money {
        val multiplier = times.asPreciseLong()
        val result = value * multiplier / PRECISION_MULTIPLIER
        return Money(result, currency)
    }

    operator fun times(times: Double): Money {
        val multiplier = times.asPreciseLong()
        val result = value * multiplier / PRECISION_MULTIPLIER
        return Money(result, currency)
    }

    operator fun div(div: Int): Money {
        return Money(value / div, currency)
    }

    operator fun div(div: Float): Money {
        val divider = div.asPreciseLong()
        val result = floor(value / divider.toDouble() * PRECISION_MULTIPLIER).toLong()
        return Money(result, currency)
    }

    operator fun div(div: Double): Money {
        val divider = div.asPreciseLong()
        val result = floor(value / divider.toDouble() * PRECISION_MULTIPLIER).toLong()
        return Money(result, currency)
    }

    override fun toString(): String {
        return platformCurrency.format(value)
    }

    enum class SupportedCurrency {
        USD,
        EUR,
        UAH
    }

    companion object {
        private const val PRECISION = 5
        private val PRECISION_MULTIPLIER = 10.0.pow(PRECISION).roundToLong()

        private fun Float.asPreciseLong(): Long {
            return (this * PRECISION_MULTIPLIER).roundToLong()
        }

        private fun Double.asPreciseLong(): Long {
            return (this * PRECISION_MULTIPLIER).roundToLong()
        }

    }
}