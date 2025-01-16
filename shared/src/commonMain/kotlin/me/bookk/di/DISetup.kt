package me.bookk.di

import me.bookk.core.LogFactory
import me.bookk.core.presentation.di.presentationCoreModule
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.storage.PreferenceProvider
import me.bookk.database.di.databaseModule
import me.bookk.di.authorization.authDiModule
import me.bookk.di.network.networkModule
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.shared.ErrorMapperImpl
import me.bookk.shared.LoggerImpl
import me.bookk.shared.data.PreferenceProviderImpl
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

fun initDI(creator: StateFactoryCreator, setup: KoinApplication.() -> Unit) {
    LogFactory.initFactory { LoggerImpl(it) }
    startKoin {
        setup()
        installModules(creator)
    }
}


@Suppress("unused")
fun initDI(creator: StateFactoryCreator) {
    initDI(creator) { }
}

private fun KoinApplication.installModules(creator: StateFactoryCreator) = modules(
    coreModule(creator),
    authDiModule(),
)

private fun coreModule(creator: StateFactoryCreator) = module {
    single<ErrorMapper> { ErrorMapperImpl() }
    single<PreferenceProvider> { PreferenceProviderImpl(get()) }
    single<StateFactoryCreator> { creator }
    includes(
        platformModule(),
        presentationCoreModule(),
        stateModule(),
        networkModule(),
        databaseModule()
    )
}

private fun stateModule() = module {
    factory<AuthStateFactory> { get<StateFactoryCreator>().createAuthFactory() }
}
