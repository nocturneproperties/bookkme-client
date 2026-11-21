package library.money.api

import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.currentLocale
import platform.Foundation.numberWithDouble

actual class Currency actual constructor(
    private val code: String,
) {
    private val fractionFormatter = formatter()

    private val wholeFormatter = formatter().apply {
        minimumFractionDigits = 0u
        maximumFractionDigits = 0u
    }

    private val symbol = fractionFormatter.currencySymbol

    actual fun code(): String {
        return code
    }

    actual fun format(value: Long): String {
        val doubleValue = value / 100.0
        val formatter = if (value % 100 == 0L) wholeFormatter else fractionFormatter
        return formatter.stringFromNumber(
            NSNumber.Companion.numberWithDouble(doubleValue),
        ).orEmpty()
    }

    actual fun symbol(): String {
        return symbol
    }

    actual fun decimalSeparator(): Char {
        return fractionFormatter.decimalSeparator().firstOrNull() ?: '.'
    }

    actual fun asString(): String {
        return "$code ($symbol)"
    }

    actual override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Currency) return false
        return other.code() == code()
    }

    actual override fun hashCode(): Int {
        return code().hashCode()
    }

    private fun formatter(): NSNumberFormatter {
        return NSNumberFormatter().apply {
            numberStyle = NSNumberFormatterCurrencyStyle
            currencyCode = code
            locale = NSLocale.Companion.currentLocale
        }
    }
}
