package me.bookk.di

import kotlinx.coroutines.CoroutineScope
import library.credentials.di.CredentialModuleFactory
import me.bookk.core.coroutine.createApplicationScope
import me.bookk.core.presentation.di.presentationCoreModule
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.database.di.databaseModule
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.business.presentation.BusinessStateFactory
import me.bookk.feature.dashboard.presentation.DashboardStateFactory
import me.bookk.feature.settings.presentation.SettingsStateFactory
import me.bookk.presentation.StateFactoryCreator
import me.bookk.shared.BuildKonfig
import me.bookk.shared.ErrorMapperImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal fun coreModule(creator: StateFactoryCreator) = module {
    single<ErrorMapper> { ErrorMapperImpl() }
    single<StateFactoryCreator> { creator }
    single<String>(named("baseUrl")) { BuildKonfig.BASE_URL }
    single<CoroutineScope> { createApplicationScope() }
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
    factory<CredentialModuleFactory> { get<StateFactoryCreator>().createCredentialModuleFactory() }
}