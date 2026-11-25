package me.bookk.client.di

import me.bookk.client.di.feature.appointmentsDiModule
import me.bookk.client.di.feature.authDiModule
import me.bookk.client.di.feature.bookingDiModule
import me.bookk.client.di.feature.businessDiModule
import me.bookk.client.di.feature.dashboardDiModule
import me.bookk.client.di.feature.employeesDiModule
import me.bookk.client.di.feature.servicesDiModule
import me.bookk.client.di.feature.settingsDiModule
import me.bookk.shared.common.di.initLogging
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

fun initClientApp(setup: KoinApplication.() -> Unit = {}) {
    if (KoinPlatform.getKoinOrNull() != null) return
    initLogging()
    startKoin {
        setup()
        installModules()
    }
}

private fun KoinApplication.installModules() {
    modules(
        coreModule(),
        libraryModule(),
        appointmentsDiModule(),
        authDiModule(),
        bookingDiModule(),
        businessDiModule(),
        dashboardDiModule(),
        employeesDiModule(),
        servicesDiModule(),
        settingsDiModule(),
    )
}
