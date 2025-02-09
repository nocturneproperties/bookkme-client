package me.bookk.presentation

import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory
import me.bookk.feature.settings.presentation.SettingsStateFactory

interface StateFactoryCreator {
    fun createAuthFactory(): AuthStateFactory
    fun createDashboardFactory(): DashboardStateFactory
    fun createSettingsFactory(): SettingsStateFactory
}