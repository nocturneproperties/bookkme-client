package me.bookk.feature.business.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapViewModel
import me.bookk.feature.business.presentation.create.CreateBusinessViewModel
import me.bookk.feature.business.presentation.dashboard.BusinessDashboardViewModel
import me.bookk.feature.business.presentation.settings.BusinessSettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal actual fun platformBusinessDiModule(): Module = module {
    factoryOf(::CreateBusinessViewModel)
    factoryOf(::BusinessBootstrapViewModel)
    factoryOf(::BusinessDashboardViewModel)
    factoryOf(::BusinessSettingsViewModel)
}

@UsedInSwift
fun createBusinessVM(): CreateBusinessViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun businessBootstrapVM(): BusinessBootstrapViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun businessDashboardVM(): BusinessDashboardViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun businessSettingsVM(): BusinessSettingsViewModel = KoinPlatform.getKoin().get()
