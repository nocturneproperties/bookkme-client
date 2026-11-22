package me.bookk.feature.dashboard.presentation.factory

import me.bookk.feature.dashboard.presentation.ClientDashboardStateFactory
import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeState
import me.bookk.feature.dashboard.presentation.screen.clienthome.WebClientHomeState

internal class WebClientDashboardStateFactory : ClientDashboardStateFactory {
    override fun createClientHomeState(): ClientHomeState {
        return WebClientHomeState()
    }
}
