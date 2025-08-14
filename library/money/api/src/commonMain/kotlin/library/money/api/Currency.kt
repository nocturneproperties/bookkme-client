package library.money.api

interface Currency {
    fun format(value: Long): String
    fun code(): String
    fun symbol(): String
    fun asString(): String
}