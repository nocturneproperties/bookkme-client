package library.money.api

interface Currency {
    fun format(value: Long): String
    fun code(): String
    fun symbol(): String
    fun decimalSeparator(): Char
    fun asString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}