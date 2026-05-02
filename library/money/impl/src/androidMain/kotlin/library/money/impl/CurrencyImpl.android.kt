package library.money.impl

import android.icu.text.DecimalFormatSymbols
import android.icu.text.NumberFormat
import library.money.api.Currency
import org.joda.money.CurrencyUnit
import android.icu.util.Currency as AndroidCurrency

internal actual class CurrencyImpl actual constructor(
    code: String
) : Currency {
    private val currencyUnit: CurrencyUnit = CurrencyUnit.of(code)
    private val formatter = NumberFormat.getCurrencyInstance().apply {
        currency = AndroidCurrency.getInstance(currencyUnit.code)
    }

    actual override fun code(): String {
        return currencyUnit.code
    }

    actual override fun format(value: Long): String {
        var divider = 1
        repeat(currencyUnit.decimalPlaces) {
            divider *= 10
        }
        val doubleValue = value / divider.toDouble()
        return formatter.format(doubleValue)
    }

    actual override fun symbol(): String {
        return currencyUnit.symbol
    }

    actual override fun decimalSeparator(): Char {
        return DecimalFormatSymbols.getInstance().decimalSeparator
    }

    actual override fun asString(): String {
        return "${currencyUnit.code} (${currencyUnit.symbol})"
    }

    actual override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is CurrencyImpl) return false
        return other.code() == code()
    }

    actual override fun hashCode(): Int {
        return code().hashCode()
    }
}