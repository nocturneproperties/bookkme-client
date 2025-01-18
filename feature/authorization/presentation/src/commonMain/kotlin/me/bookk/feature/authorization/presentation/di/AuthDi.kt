package me.bookk.feature.authorization.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.authorization.presentation.sign_in.SignInViewModel
import me.bookk.feature.authorization.presentation.sign_up.SignUpViewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal expect fun platformAuthDiModule(): Module

fun authPresentationModule() = module {
    includes(platformAuthDiModule())
}

@UsedInSwift
fun signUpVM(): SignUpViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun signInVM(): SignInViewModel = KoinPlatform.getKoin().get()