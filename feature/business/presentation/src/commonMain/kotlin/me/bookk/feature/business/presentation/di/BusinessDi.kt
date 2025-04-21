package me.bookk.feature.business.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformBusinessDiModule(): Module

fun businessPresentationModule() = module {
    includes(platformBusinessDiModule())
}