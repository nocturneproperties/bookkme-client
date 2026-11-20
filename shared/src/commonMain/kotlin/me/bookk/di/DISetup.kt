package me.bookk.di

import me.bookk.di.feature.appointmentsDiModule
import me.bookk.di.feature.authDiModule
import me.bookk.di.feature.businessDiModule
import me.bookk.di.feature.clientsDiModule
import me.bookk.di.feature.dashboardDiModule
import me.bookk.di.feature.employeesDiModule
import me.bookk.di.feature.servicesDiModule
import me.bookk.di.feature.settingsDiModule
import me.bookk.presentation.StateFactoryCreator
import me.bookk.shared.common.di.initLogging
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

fun initBusinessApp(creator: StateFactoryCreator, setup: KoinApplication.() -> Unit) {
    initLogging()
    startKoin {
        setup()
        installModules(creator)
    }
}

@Suppress("unused")
fun initBusinessApp(creator: StateFactoryCreator) {
    initBusinessApp(creator) { }
}

private fun KoinApplication.installModules(creator: StateFactoryCreator) = modules(
    coreModule(creator),
    libraryModule(),
    appointmentsDiModule(),
    authDiModule(),
    businessDiModule(),
    clientsDiModule(),
    dashboardDiModule(),
    employeesDiModule(),
    servicesDiModule(),
    settingsDiModule(),
)
