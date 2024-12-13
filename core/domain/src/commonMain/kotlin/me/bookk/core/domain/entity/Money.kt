package me.bookk.core.domain.entity

data class Money(
    val value: Long,
    val currency: Currency,
) {
    enum class Currency {
        USD,
        EUR
    }

    operator fun times(times: Int): Money {
        return Money(value * times, currency)
    }

    operator fun times(times: Float): Money {
        return Money((value * times).toLong(), currency)
    }

    operator fun div(div: Int): Money {
        return Money(value / div, currency)
    }

    operator fun div(div: Float): Money {
        return Money((value / div).toLong(), currency)
    }
}

val Money.Currency.displaySymbol: String
    get() = when (this) {
        Money.Currency.USD -> "$"
        Money.Currency.EUR -> "€"
    }

val Money.displayValue: String
    get() = "${if (value % 100 == 0L) (value / 100).toInt() else value / 100f}"

val Money.displayPrice: String
    get() = "$displayValue${currency.displaySymbol}"

val Money.displayPriceRounded: String
    get() = "${(value / 100f).toString(decimalPlaces = 1)}${currency.displaySymbol}"