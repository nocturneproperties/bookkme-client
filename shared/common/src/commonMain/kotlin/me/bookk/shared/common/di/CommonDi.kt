package me.bookk.shared.common.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import me.bookk.core.Logger
import me.bookk.core.config.SiteUrl
import me.bookk.core.coroutine.createApplicationScope
import me.bookk.core.presentation.di.presentationCoreModule
import me.bookk.core.presentation.error.ErrorMapper
import me.bookk.shared.common.BuildKonfig
import me.bookk.shared.common.ErrorMapperImpl
import me.bookk.shared.common.LoggerImpl
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun initLogging() {
    if (BuildKonfig.DEBUG) Napier.base(DebugAntilog())
    Logger.initFactory { LoggerImpl(it) }
}

fun commonModule(): Module {
    return module {
        single<ErrorMapper> { ErrorMapperImpl() }
        single<CoroutineScope> { createApplicationScope() }
        single { SiteUrl(BuildKonfig.SITE_URL) }
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
            networkModule(),
        )
    }
}
