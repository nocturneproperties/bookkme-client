package library.money.impl

import library.money.api.Currency
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale
import platform.Foundation.numberWithDouble

internal actual class CurrencyImpl actual constructor(
    private val code: String
) : Currency {
    private val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        currencyCode = code
        locale = NSLocale.currentLocale
    }

    private val symbol = formatter.currencySymbol

    actual override fun code(): String {
        return code
    }

    actual override fun format(value: Long): String {
        val doubleValue = value / 100.0
        return formatter.stringFromNumber(NSNumber.numberWithDouble(doubleValue)).orEmpty()
    }

    actual override fun symbol(): String {
        return symbol
    }

    actual override fun decimalSeparator(): Char {
        return formatter.decimalSeparator().firstOrNull() ?: '.'
    }

    actual override fun asString(): String {
        return "$code ($symbol)"
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