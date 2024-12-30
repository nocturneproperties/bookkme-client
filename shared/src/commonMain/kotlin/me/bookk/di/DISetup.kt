package me.bookk.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.serialization.protobuf.ProtoBuf
import me.bookk.core.LogFactory
import me.bookk.core.presentation.di.presentationCoreModule
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.storage.PreferenceProvider
import me.bookk.database.di.databaseModule
import me.bookk.di.authorization.authDiModule
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.shared.BuildKonfig
import me.bookk.shared.ErrorMapperImpl
import me.bookk.shared.LoggerImpl
import me.bookk.shared.data.PreferenceProviderImpl
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module
import io.ktor.client.plugins.logging.Logger as KtorLogger

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

private fun networkModule() = module {
    single {
        HttpClient {
            expectSuccess = true
            install(Logging) {
                logger = KtorLogger.DEFAULT
                level = LogLevel.ALL
            }
            install(ContentNegotiation) {
                protobuf(ProtoBuf { encodeDefaults = true })
            }
            defaultRequest {
                url(BuildKonfig.BASE_URL)
            }
        }
    }
}
