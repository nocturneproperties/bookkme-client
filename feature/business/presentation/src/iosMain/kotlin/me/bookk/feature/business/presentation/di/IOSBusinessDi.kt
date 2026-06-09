package me.bookk.feature.business.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.business.presentation.screen.bootstrap.BusinessBootstrapViewModel
import me.bookk.feature.business.presentation.screen.create.CreateBusinessViewModel
import me.bookk.feature.business.presentation.screen.dashboard.BusinessDashboardViewModel
import me.bookk.feature.business.presentation.screen.settings.BusinessSettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.uuid.Uuid

internal actual fun platformBusinessDiModule(): Module = module {
    factoryOf(::CreateBusinessViewModel)
    factoryOf(::BusinessBootstrapViewModel)
    factoryOf(::BusinessDashboardViewModel)
    factory { BusinessSettingsViewModel(it.get(), get(), get(), get(), get(), get()) }
}

@UsedInSwift
fun createBusinessVM(): CreateBusinessViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun businessBootstrapVM(): BusinessBootstrapViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun businessDashboardVM(): BusinessDashboardViewModel = KoinPlatform.getKoin().get()

@UsedInSwift
fun businessSettingsVM(id: Uuid): BusinessSettingsViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(id) })
