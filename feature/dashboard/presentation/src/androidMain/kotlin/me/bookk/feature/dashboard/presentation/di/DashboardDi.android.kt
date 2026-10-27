package me.bookk.feature.dashboard.presentation.di

import me.bookk.feature.dashboard.presentation.DashboardViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformDashboardDiModule(): Module = module {
    viewModelOf(::DashboardViewModel)
}