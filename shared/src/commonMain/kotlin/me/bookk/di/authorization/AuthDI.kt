package me.bookk.di.authorization

import me.bookk.feature.authorization.presentation.di.authPresentationModule
import org.koin.dsl.module

fun authDiModule() = module {
    includes(
        authPresentationModule()
    )
}