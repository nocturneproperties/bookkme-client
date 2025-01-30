package me.bookk.feature.authorization.presentation.di

import me.bookk.feature.authorization.presentation.bootstrap.BootstrapViewModel
import me.bookk.feature.authorization.presentation.sign_in.SignInViewModel
import me.bookk.feature.authorization.presentation.sign_up.SignUpViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal actual fun platformAuthDiModule(): Module = module {
    factoryOf(::SignUpViewModel)
    factoryOf(::SignInViewModel)
    factoryOf(::BootstrapViewModel)
}