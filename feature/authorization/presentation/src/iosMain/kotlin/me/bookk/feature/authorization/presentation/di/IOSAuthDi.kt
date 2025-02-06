package me.bookk.feature.authorization.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapViewModel
import me.bookk.feature.authorization.presentation.sign_in.SignInViewModel
import me.bookk.feature.authorization.presentation.sign_up.SignUpViewModel
import me.bookk.feature.authorization.presentation.troubleshoot.TroubleshootViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal actual fun platformAuthDiModule(): Module = module {
    factoryOf(::SignUpViewModel)
    factoryOf(::SignInViewModel)
    factoryOf(::BootstrapViewModel)
    factoryOf(::TroubleshootViewModel)
}

@UsedInSwift
fun signUpVM(): SignUpViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun signInVM(): SignInViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun bootstrapVM(): BootstrapViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun troubleshootVM(): TroubleshootViewModel = KoinPlatform.getKoin().get()