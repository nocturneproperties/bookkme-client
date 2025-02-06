package me.bookk.feature.authorization.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformAuthDiModule(): Module

fun authPresentationModule() = module {
    includes(platformAuthDiModule())
}