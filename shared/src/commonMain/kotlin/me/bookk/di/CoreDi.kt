package me.bookk.di

import library.crashreporting.api.CrashReporter
import library.credentials.api.PasskeyCredentialUpdater
import library.credentials.di.CredentialModuleFactory
import library.picker.PickOptionStateFactory
import me.bookk.database.business.di.businessDatabaseModule
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.EmailVerificationStateFactory
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory
import me.bookk.feature.employees.presentation.EmployeesStateFactory
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.presentation.StateFactoryCreator
import me.bookk.shared.common.di.commonModule
import org.koin.dsl.bind
import org.koin.dsl.module

internal fun coreModule(creator: StateFactoryCreator) = module {
    single<StateFactoryCreator> { creator }
    includes(
        commonModule(),
        stateModule(),
        businessDatabaseModule(),
    )
}

private fun stateModule() = module {
    factory<AuthStateFactory> { get<StateFactoryCreator>().createAuthFactory() } bind
        EmailVerificationStateFactory::class
    factory<DashboardStateFactory> { get<StateFactoryCreator>().createDashboardFactory() }
    factory<SettingsStateFactory> { get<StateFactoryCreator>().createSettingsFactory() }
    factory<BusinessStateFactory> { get<StateFactoryCreator>().createBusinessFactory() }
    factory<ClientsStateFactory> { get<StateFactoryCreator>().createClientsFactory() }
    factory<EmployeesStateFactory> { get<StateFactoryCreator>().createEmployeesFactory() }
    factory<ServicesStateFactory> { get<StateFactoryCreator>().createServicesFactory() }
    factory<CredentialModuleFactory> { get<StateFactoryCreator>().createCredentialModuleFactory() }
    single<PasskeyCredentialUpdater> { get<StateFactoryCreator>().createPasskeyCredentialUpdater() }
    single<CrashReporter> { get<StateFactoryCreator>().createCrashReporter() }
    factory<PickOptionStateFactory> { get<StateFactoryCreator>().createPickOptionFactory() }
    factory<AppointmentsStateFactory> { get<StateFactoryCreator>().createAppointmentsFactory() }
}
