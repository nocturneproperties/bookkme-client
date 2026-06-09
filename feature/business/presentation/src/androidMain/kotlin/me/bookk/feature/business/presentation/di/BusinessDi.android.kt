package me.bookk.feature.business.presentation.di

import me.bookk.feature.business.presentation.screen.bootstrap.BusinessBootstrapViewModel
import me.bookk.feature.business.presentation.screen.create.CreateBusinessViewModel
import me.bookk.feature.business.presentation.screen.dashboard.BusinessDashboardViewModel
import me.bookk.feature.business.presentation.screen.settings.BusinessSettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformBusinessDiModule(): Module = module {
    viewModelOf(::CreateBusinessViewModel)
    viewModelOf(::BusinessBootstrapViewModel)
    viewModelOf(::BusinessDashboardViewModel)
    viewModel { BusinessSettingsViewModel(it.get(), get(), get(), get(), get(), get()) }
}