package me.bookk.feature.authorization.presentation.di

import me.bookk.feature.authorization.presentation.factory.WebTelegramSignInStateFactory
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInStateFactory
import me.bookk.feature.authorization.presentation.screen.telegram.TelegramSignInViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformAuthDiModule(): Module = module {
    factoryOf(::WebTelegramSignInStateFactory) bind TelegramSignInStateFactory::class
    factoryOf(::TelegramSignInViewModel)
}
