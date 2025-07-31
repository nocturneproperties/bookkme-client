package library.money.api

interface Currency {
    fun format(value: Long): String
    fun symbol(): String
    fun asString(): String
}