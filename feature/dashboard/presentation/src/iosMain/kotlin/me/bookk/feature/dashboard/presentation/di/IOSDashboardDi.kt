package me.bookk.feature.dashboard.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.dashboard.presentation.DashboardViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal actual fun platformDashboardDiModule(): Module = module {
    factoryOf(::DashboardViewModel)
}

@UsedInSwift
fun dashboardVM(): DashboardViewModel = KoinPlatform.getKoin().get()