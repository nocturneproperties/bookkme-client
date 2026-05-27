package me.bookk.presentation

import library.credentials.di.CredentialModuleFactory
import library.picker.PickOptionStateFactory
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.settings.presentation.SettingsStateFactory

interface StateFactoryCreator {
    fun createAuthFactory(): AuthStateFactory
    fun createDashboardFactory(): DashboardStateFactory
    fun createSettingsFactory(): SettingsStateFactory
    fun createBusinessFactory(): BusinessStateFactory
    fun createCredentialModuleFactory(): CredentialModuleFactory
    fun createClientsFactory(): ClientsStateFactory
    fun createServicesFactory(): ServicesStateFactory
    fun createPickOptionFactory(): PickOptionStateFactory
}
