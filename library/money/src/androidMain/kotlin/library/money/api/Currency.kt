package library.money.api

import org.joda.money.CurrencyUnit
import java.text.DecimalFormatSymbols
import java.text.NumberFormat

actual class Currency actual constructor(
    code: String,
) {
    private val currencyUnit: CurrencyUnit = CurrencyUnit.of(code)
    private val fractionFormatter by lazy { formatter(currencyUnit.decimalPlaces) }
    private val wholeFormatter by lazy { formatter(0) }

    actual fun code(): String {
        return currencyUnit.code
    }

    actual fun format(value: Long): String {
        var divider = 1
        repeat(currencyUnit.decimalPlaces) {
            divider *= 10
        }
        val doubleValue = value / divider.toDouble()
        val formatter = if (value % divider == 0L) wholeFormatter else fractionFormatter
        return formatter.format(doubleValue)
    }

    actual fun symbol(): String {
        return currencyUnit.symbol
    }

    actual fun decimalSeparator(): Char {
        return DecimalFormatSymbols.getInstance().decimalSeparator
    }

    actual fun asString(): String {
        return "${currencyUnit.code} (${currencyUnit.symbol})"
    }

    actual override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Currency) return false
        return other.code() == code()
    }

    actual override fun hashCode(): Int {
        return code().hashCode()
    }

    private fun formatter(fractionDigits: Int): NumberFormat {
        return NumberFormat.getCurrencyInstance().apply {
            currency = java.util.Currency.getInstance(currencyUnit.code)
            minimumFractionDigits = fractionDigits
            maximumFractionDigits = fractionDigits
        }
    }
}
