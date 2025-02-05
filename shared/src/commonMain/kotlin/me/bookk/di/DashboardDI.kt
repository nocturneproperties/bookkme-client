package me.bookk.di

import me.bookk.feature.dashboard.presentation.di.dashboardPresentationModule
import org.koin.dsl.module

internal fun dashboardDiModule() = module {
    includes(
        dashboardPresentationModule(),
    )
}