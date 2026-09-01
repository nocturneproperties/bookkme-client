package me.bookk.presentation

import library.credentials.di.CredentialModuleFactory
import library.picker.PickOptionStateFactory
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory
import me.bookk.feature.employees.presentation.EmployeesStateFactory
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.settings.presentation.SettingsStateFactory

interface StateFactoryCreator {
    fun createAppointmentsFactory(): AppointmentsStateFactory
    fun createAuthFactory(): AuthStateFactory
    fun createBusinessFactory(): BusinessStateFactory
    fun createClientsFactory(): ClientsStateFactory
    fun createCredentialModuleFactory(): CredentialModuleFactory
    fun createDashboardFactory(): DashboardStateFactory
    fun createEmployeesFactory(): EmployeesStateFactory
    fun createPickOptionFactory(): PickOptionStateFactory
    fun createServicesFactory(): ServicesStateFactory
    fun createSettingsFactory(): SettingsStateFactory
}
