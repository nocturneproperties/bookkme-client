package me.bookk.feature.authorization.presentation.di

import me.bookk.feature.authorization.presentation.ClientStateFactory
import me.bookk.feature.authorization.presentation.factory.WebClientStateFactory
import me.bookk.feature.authorization.presentation.screen.clientbootstrap.ClientBootstrapViewModel
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformAuthDiModule(): Module = module {
    factoryOf(::WebClientStateFactory) bind ClientStateFactory::class
    factoryOf(::ClientBootstrapViewModel)
    factoryOf(::TelegramSignInViewModel)
}
