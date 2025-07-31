package library.money.api

interface CurrencyFactoryImpl {
    fun forCode(code: String): Currency
}