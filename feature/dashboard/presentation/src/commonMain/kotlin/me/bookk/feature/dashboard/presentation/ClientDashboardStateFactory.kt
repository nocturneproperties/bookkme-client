package me.bookk.feature.dashboard.presentation

import me.bookk.feature.dashboard.presentation.screen.clienthome.ClientHomeState

interface ClientDashboardStateFactory {
    fun createClientHomeState(): ClientHomeState
}
