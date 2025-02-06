package me.bookk.feature.dashboard.presentation.di

import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun platformDashboardDiModule(): Module

fun dashboardPresentationModule() = module {
    includes(platformDashboardDiModule())
}