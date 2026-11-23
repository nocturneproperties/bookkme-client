package me.bookk.feature.dashboard.presentation.di

import me.bookk.feature.dashboard.presentation.ClientDashboardStateFactory
import me.bookk.feature.dashboard.presentation.factory.WebClientDashboardStateFactory
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

internal actual fun platformDashboardDiModule(): Module = module {
    factoryOf(::WebClientDashboardStateFactory) bind ClientDashboardStateFactory::class
    factoryOf(::ClientHomeViewModel)
}
