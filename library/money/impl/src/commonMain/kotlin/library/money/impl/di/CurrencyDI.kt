package library.money.impl.di

import library.money.api.CurrencyFactoryImpl
import library.money.impl.CommonCurrencyFactory
import org.koin.dsl.module

fun moneyModule() = module {
    single<CurrencyFactoryImpl> { CommonCurrencyFactory() }
}