package me.bookk.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.protobuf.protobuf
import kotlinx.serialization.protobuf.ProtoBuf
import me.bookk.core.presentation.di.presentationCoreModule
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.core.storage.PreferenceProvider
import me.bookk.database.di.databaseModule
import me.bookk.di.authorization.authDiModule
import me.bookk.shared.BuildKonfig
import me.bookk.shared.ErrorMapperImpl
import me.bookk.shared.data.PreferenceProviderImpl
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module
import io.ktor.client.plugins.logging.Logger as KtorLogger

fun initDI(setup: KoinApplication.() -> Unit) {
    startKoin {
        setup()
        installModules()
    }
}

fun initDI() {
    initDI { }
}

private fun KoinApplication.installModules() = modules(
    coreModule(),
    authDiModule()
)

private fun coreModule() = module {
    single<ErrorMapper> { ErrorMapperImpl() }
    single<PreferenceProvider> { PreferenceProviderImpl(get()) }
    includes(
        platformModule(),
        presentationCoreModule(),
        networkModule(),
        databaseModule()
    )
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
