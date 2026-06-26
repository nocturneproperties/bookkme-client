package library.money.api

expect class Currency(code: String) {
    fun format(value: Long): String
    fun code(): String
    fun symbol(): String
    fun decimalSeparator(): Char
    fun asString(): String
    override fun equals(other: Any?): Boolean
    override fun hashCode(): Int
}