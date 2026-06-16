package me.bookk.feature.appointments.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateViewModel
import me.bookk.feature.appointments.presentation.screen.requestlist.AppointmentListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.uuid.Uuid

internal actual fun platformAppointmentsDiModule(): Module = module {
    factoryOf(::AppointmentListViewModel)
    factoryOf(::AppointmentCreateViewModel)
}

@UsedInSwift
fun appointmentListVM(): AppointmentListViewModel =
    KoinPlatform.getKoin().get()

@UsedInSwift
fun appointmentCreateVM(businessId: Uuid): AppointmentCreateViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(businessId) })
