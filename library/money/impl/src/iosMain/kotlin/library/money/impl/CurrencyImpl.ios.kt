package library.money.impl

import library.money.api.Currency
import platform.Foundation.NSLocale
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterCurrencyStyle
import platform.Foundation.availableLocaleIdentifiers
import platform.Foundation.currencyCode
import platform.Foundation.currencySymbol
import platform.Foundation.currentLocale
import platform.Foundation.numberWithDouble

internal actual class CurrencyImpl actual constructor(
    private val code: String
) : Currency {

    private val fittingLocales = NSLocale.availableLocaleIdentifiers()
        .map { NSLocale(localeIdentifier = it?.toString().orEmpty()) }
        .filter { code == it.currencyCode }

    private val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterCurrencyStyle
        currencyCode = code
        locale = fittingLocales.firstOrNull() ?: NSLocale.currentLocale
    }

    private val symbol = fittingLocales.firstOrNull()?.currencySymbol.orEmpty()

    override fun code(): String {
        return code
    }

    override fun format(value: Long): String {
        val doubleValue = value / 100.0
        return formatter.stringFromNumber(NSNumber.numberWithDouble(doubleValue)).orEmpty()
    }

    override fun symbol(): String {
        return symbol
    }

    override fun asString(): String {
        return "$code ($symbol)"
    }

}