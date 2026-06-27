package me.bookk.feature.appointments.presentation.di

import me.bookk.core.UsedInSwift
import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateViewModel
import me.bookk.feature.appointments.presentation.screen.details.AppointmentDetailsViewModel
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryViewModel
import me.bookk.feature.appointments.presentation.screen.list.AppointmentListViewModel
import me.bookk.feature.appointments.presentation.screen.request.AppointmentRequestViewModel
import me.bookk.feature.appointments.presentation.screen.settings.AppointmentSettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.uuid.Uuid

internal actual fun platformAppointmentsDiModule(): Module = module {
    factoryOf(::AppointmentListViewModel)
    factoryOf(::AppointmentCreateViewModel)
    factoryOf(::AppointmentDetailsViewModel)
    factoryOf(::AppointmentSettingsViewModel)
    factoryOf(::AppointmentHistoryViewModel)
    factoryOf(::AppointmentRequestViewModel)
}

@UsedInSwift
fun appointmentListVM(): AppointmentListViewModel =
    KoinPlatform.getKoin().get()

@UsedInSwift
fun appointmentCreateVM(businessId: Uuid): AppointmentCreateViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(businessId) })

@UsedInSwift
fun appointmentDetailsVM(appointmentId: Uuid): AppointmentDetailsViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(appointmentId) })

@UsedInSwift
fun appointmentSettingsVM(businessId: Uuid): AppointmentSettingsViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(businessId) })

@UsedInSwift
fun appointmentHistoryVM(businessId: Uuid): AppointmentHistoryViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(businessId) })

@UsedInSwift
fun appointmentRequestVM(businessId: Uuid): AppointmentRequestViewModel =
    KoinPlatform.getKoin().get(parameters = { parametersOf(businessId) })
