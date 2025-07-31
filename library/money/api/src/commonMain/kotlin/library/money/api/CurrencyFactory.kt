package library.money.api

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CurrencyFactory : KoinComponent {

    private val factory: CurrencyFactoryImpl by inject()

    fun forCode(code: String): Currency {
        return factory.forCode(code)
    }
}