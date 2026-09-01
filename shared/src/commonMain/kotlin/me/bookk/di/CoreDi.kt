package me.bookk.di

import kotlinx.coroutines.CoroutineScope
import library.credentials.di.CredentialModuleFactory
import library.picker.PickOptionStateFactory
import me.bookk.core.coroutine.createApplicationScope
import me.bookk.core.presentation.di.presentationCoreModule
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.database.di.databaseModule
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.clients.presentation.ClientsStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory
import me.bookk.feature.employees.presentation.EmployeesStateFactory
import me.bookk.feature.services.presentation.ServicesStateFactory
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.presentation.StateFactoryCreator
import me.bookk.shared.BuildKonfig
import me.bookk.shared.ErrorMapperImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal fun coreModule(creator: StateFactoryCreator) = module {
    single<ErrorMapper> { ErrorMapperImpl() }
    single<StateFactoryCreator> { creator }
    single<CoroutineScope> { createApplicationScope() }
    single<String>(named("baseUrl")) { BuildKonfig.BASE_URL }
    single<String>(named("relyingParty")) {
        BuildKonfig.BASE_URL
            .removeSuffix("/api")
            .removePrefix("https://")
            .split(".")
            .takeLast(2)
            .joinToString(".")
    }
    includes(
        presentationCoreModule(),
        stateModule(),
        networkModule(),
        databaseModule()
    )
}

private fun stateModule() = module {
    factory<AuthStateFactory> { get<StateFactoryCreator>().createAuthFactory() }
    factory<DashboardStateFactory> { get<StateFactoryCreator>().createDashboardFactory() }
    factory<SettingsStateFactory> { get<StateFactoryCreator>().createSettingsFactory() }
    factory<BusinessStateFactory> { get<StateFactoryCreator>().createBusinessFactory() }
    factory<ClientsStateFactory> { get<StateFactoryCreator>().createClientsFactory() }
    factory<EmployeesStateFactory> { get<StateFactoryCreator>().createEmployeesFactory() }
    factory<ServicesStateFactory> { get<StateFactoryCreator>().createServicesFactory() }
    factory<CredentialModuleFactory> { get<StateFactoryCreator>().createCredentialModuleFactory() }
    factory<PickOptionStateFactory> { get<StateFactoryCreator>().createPickOptionFactory() }
    factory<AppointmentsStateFactory> { get<StateFactoryCreator>().createAppointmentsFactory() }
}