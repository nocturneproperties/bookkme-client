package me.bookk.feature.appointments.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.appointments.presentation.screen.requestlist.AppointmentRequestListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform

internal actual fun platformAppointmentsDiModule(): Module = module {
    factoryOf(::AppointmentRequestListViewModel)
}

@UsedInSwift
fun appointmentRequestListVM(): AppointmentRequestListViewModel =
    KoinPlatform.getKoin().get()
