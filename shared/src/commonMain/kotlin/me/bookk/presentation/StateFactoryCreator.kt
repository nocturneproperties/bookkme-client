package me.bookk.presentation

import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory

interface StateFactoryCreator {
    fun createAuthFactory(): AuthStateFactory
    fun createDashboardFactory(): DashboardStateFactory
}