package me.bookk.android

import library.credentials.di.CredentialModuleFactory
import library.credentials.impl.AndroidCredentialFactory
import library.picker.AndroidPickOptionStateFactory
import library.picker.PickOptionStateFactory
import me.bookk.feature.appointments.presentation.AndroidAppointmentsStateFactory
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.factory.AndroidAuthStateFactory
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.business.presentation.factory.AndroidBusinessStateFactory
import me.bookk.feature.clients.presentation.AndroidClientsStateFactory
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.dashboard.presentation.AndroidDashboardStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory
import me.bookk.feature.employees.presentation.AndroidEmployeesStateFactory
import me.bookk.feature.employees.presentation.EmployeesStateFactory
import me.bookk.feature.services.presentation.AndroidServicesStateFactory
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.feature.settings.presentation.factory.AndroidSettingsStateFactory
import me.bookk.presentation.StateFactoryCreator

class AndroidStateFactoryCreator : StateFactoryCreator {
    override fun createAppointmentsFactory(): AppointmentsStateFactory {
        return AndroidAppointmentsStateFactory()
    }

    override fun createAuthFactory(): AuthStateFactory {
        return AndroidAuthStateFactory()
    }

    override fun createBusinessFactory(): BusinessStateFactory {
        return AndroidBusinessStateFactory()
    }

    override fun createClientsFactory(): ClientsStateFactory {
        return AndroidClientsStateFactory()
    }

    override fun createCredentialModuleFactory(): CredentialModuleFactory {
        return AndroidCredentialFactory()
    }

    override fun createDashboardFactory(): DashboardStateFactory {
        return AndroidDashboardStateFactory()
    }

    override fun createEmployeesFactory(): EmployeesStateFactory {
        return AndroidEmployeesStateFactory()
    }

    override fun createPickOptionFactory(): PickOptionStateFactory {
        return AndroidPickOptionStateFactory()
    }

    override fun createServicesFactory(): ServicesStateFactory {
        return AndroidServicesStateFactory()
    }

    override fun createSettingsFactory(): SettingsStateFactory {
        return AndroidSettingsStateFactory()
    }
}
