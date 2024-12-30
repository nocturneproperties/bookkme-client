package me.bookk.feature.authorization.presentation.di

import me.bookk.feature.authorization.presentation.sign_up.SignUpViewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal expect fun platformAuthDiModule(): Module

fun authPresentationModule() = module {
    includes(platformAuthDiModule())
}

fun signUpVM(): SignUpViewModel = KoinPlatform.getKoin().get()