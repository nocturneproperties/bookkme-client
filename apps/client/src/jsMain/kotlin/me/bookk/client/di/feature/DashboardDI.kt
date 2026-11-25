package me.bookk.client.di.feature

import me.bookk.feature.dashboard.presentation.di.dashboardPresentationModule
import org.koin.dsl.module

internal fun dashboardDiModule() = module {
    includes(
        dashboardPresentationModule(),
    )
}
