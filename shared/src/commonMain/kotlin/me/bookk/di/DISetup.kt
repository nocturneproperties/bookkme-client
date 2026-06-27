package me.bookk.di

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import me.bookk.core.Logger
import me.bookk.di.feature.appointmentsDiModule
import me.bookk.di.feature.authDiModule
import me.bookk.di.feature.businessDiModule
import me.bookk.di.feature.clientsDiModule
import me.bookk.di.feature.dashboardDiModule
import me.bookk.di.feature.servicesDiModule
import me.bookk.di.feature.settingsDiModule
import me.bookk.presentation.StateFactoryCreator
import me.bookk.shared.BuildKonfig
import me.bookk.shared.LoggerImpl
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initDI(creator: StateFactoryCreator, setup: KoinApplication.() -> Unit) {
    if (BuildKonfig.DEBUG) Napier.base(DebugAntilog())
    Logger.initFactory { LoggerImpl(it) }
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
    libraryModule(),
    appointmentsDiModule(),
    authDiModule(),
    businessDiModule(),
    clientsDiModule(),
    dashboardDiModule(),
    servicesDiModule(),
    settingsDiModule()
)
