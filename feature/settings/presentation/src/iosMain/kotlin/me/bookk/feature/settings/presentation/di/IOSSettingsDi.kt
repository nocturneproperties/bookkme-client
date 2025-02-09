package me.bookk.feature.settings.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.settings.presentation.dashboard.SettingsDashboardViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal actual fun platformSettingsDiModule(): Module = module {
    factoryOf(::SettingsDashboardViewModel)
}

@UsedInSwift
fun settingsVM(): SettingsDashboardViewModel = KoinPlatform.getKoin().get()