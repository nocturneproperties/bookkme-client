package me.bookk.feature.settings.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformSettingsDiModule(): Module

fun settingsPresentationModule() = module {
    includes(platformSettingsDiModule())
}