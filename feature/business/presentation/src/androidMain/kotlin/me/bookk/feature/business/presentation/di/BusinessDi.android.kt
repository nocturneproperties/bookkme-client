package me.bookk.feature.business.presentation.di

import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapViewModel
import me.bookk.feature.business.presentation.clients.list.ClientsListViewModel
import me.bookk.feature.business.presentation.create.CreateBusinessViewModel
import me.bookk.feature.business.presentation.dashboard.BusinessDashboardViewModel
import me.bookk.feature.business.presentation.settings.BusinessSettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformBusinessDiModule(): Module = module {
    viewModelOf(::CreateBusinessViewModel)
    viewModelOf(::BusinessBootstrapViewModel)
    viewModelOf(::BusinessDashboardViewModel)
    viewModelOf(::ClientsListViewModel)
    viewModel { BusinessSettingsViewModel(it.get(), get(), get(), get(), get(), get()) }
}