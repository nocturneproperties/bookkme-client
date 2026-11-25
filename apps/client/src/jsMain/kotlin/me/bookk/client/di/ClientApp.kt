package me.bookk.client.di

import library.cache.impl.di.cacheModule
import library.device.impl.di.deviceModule
import library.files.impl.di.filesModule
import me.bookk.database.client.di.clientDatabaseModule
import me.bookk.feature.appointments.data.di.clientAppointmentsDataModule
import me.bookk.feature.appointments.domain.impl.di.clientAppointmentsDomainModule
import me.bookk.feature.authorization.data.di.authDataModule
import me.bookk.feature.authorization.domain.impl.di.authDomainModule
import me.bookk.feature.authorization.presentation.di.authPresentationModule
import me.bookk.feature.booking.data.di.bookingDataModule
import me.bookk.feature.booking.domain.impl.di.bookingDomainModule
import me.bookk.feature.booking.presentation.di.bookingPresentationModule
import me.bookk.feature.business.data.di.clientBusinessDataModule
import me.bookk.feature.business.domain.impl.di.clientBusinessDomainModule
import me.bookk.feature.dashboard.presentation.di.dashboardPresentationModule
import me.bookk.feature.employees.data.di.clientEmployeesDataModule
import me.bookk.feature.employees.domain.impl.di.clientEmployeesDomainModule
import me.bookk.feature.services.data.di.servicesDataModule
import me.bookk.feature.services.domain.impl.di.clientServicesDomainModule
import me.bookk.feature.settings.data.di.settingsDataModule
import me.bookk.feature.settings.domain.impl.di.settingsDomainModule
import me.bookk.feature.settings.presentation.di.settingsPresentationModule
import me.bookk.shared.common.di.commonModule
import me.bookk.shared.common.di.initLogging
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform

fun initClientApp(setup: KoinApplication.() -> Unit = {}) {
    if (KoinPlatform.getKoinOrNull() != null) return
    initLogging()
    startKoin {
        setup()
        modules(
            commonModule(),
            deviceModule(),
            cacheModule(),
            filesModule(),
            clientDatabaseModule(),
            authDomainModule(),
            authDataModule(),
            settingsDomainModule(),
            settingsDataModule(),
            authPresentationModule(),
            clientAppointmentsDomainModule(),
            clientAppointmentsDataModule(),
            clientBusinessDomainModule(),
            clientBusinessDataModule(),
            clientServicesDomainModule(),
            servicesDataModule(),
            clientEmployeesDomainModule(),
            clientEmployeesDataModule(),
            dashboardPresentationModule(),
            bookingDomainModule(),
            bookingDataModule(),
            bookingPresentationModule(),
            settingsPresentationModule(),
        )
    }
}
