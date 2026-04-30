package library.money.impl

import library.money.api.Currency

internal expect class CurrencyImpl(code: String) : Currency {
    override fun format(value: Long): String
    override fun code(): String
    override fun symbol(): String
    override fun decimalSeparator(): Char
    override fun asString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}