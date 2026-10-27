package me.bookk.feature.services.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformServicesDiModule(): Module

fun servicesPresentationModule() = module {
    includes(platformServicesDiModule())
}