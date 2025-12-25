package library.money.impl

import library.money.api.Currency
import library.money.api.CurrencyFactoryImpl

internal class CommonCurrencyFactory : CurrencyFactoryImpl {
    override fun forCode(code: String): Currency {
        return CurrencyImpl(code)
    }
}