package library.money.api

import android.icu.text.DecimalFormatSymbols
import android.icu.text.NumberFormat
import org.joda.money.CurrencyUnit

actual class Currency actual constructor(
    code: String
) {
    private val currencyUnit: CurrencyUnit = CurrencyUnit.of(code)
    private val formatter by lazy {
        NumberFormat.getCurrencyInstance().apply {
            currency = android.icu.util.Currency.getInstance(currencyUnit.code)
        }
    }

    actual fun code(): String {
        return currencyUnit.code
    }

    actual fun format(value: Long): String {
        var divider = 1
        repeat(currencyUnit.decimalPlaces) {
            divider *= 10
        }
        val doubleValue = value / divider.toDouble()
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
}