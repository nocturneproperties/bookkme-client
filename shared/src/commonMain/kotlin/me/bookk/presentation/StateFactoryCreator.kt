package me.bookk.presentation

import library.credentials.di.CredentialModuleFactory
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory
import me.bookk.feature.settings.presentation.SettingsStateFactory

interface StateFactoryCreator {
    fun createAuthFactory(): AuthStateFactory
    fun createDashboardFactory(): DashboardStateFactory
    fun createSettingsFactory(): SettingsStateFactory
    fun createBusinessFactory(): BusinessStateFactory
    fun createCredentialModuleFactory(): CredentialModuleFactory
}