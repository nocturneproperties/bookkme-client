package me.bookk.feature.clients.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformClientsDiModule(): Module

fun clientsPresentationModule() = module {
    includes(platformClientsDiModule())
}