package library.money.impl

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

    override fun code(): String {
        return currencyUnit.code
    }

    override fun format(value: Long): String {
        val doubleValue = value / (10 * currencyUnit.decimalPlaces)
        return formatter.format(doubleValue)
    }

    override fun symbol(): String {
        return currencyUnit.symbol
    }

    override fun asString(): String {
        return "${currencyUnit.code} (${currencyUnit.symbol})"
    }
}