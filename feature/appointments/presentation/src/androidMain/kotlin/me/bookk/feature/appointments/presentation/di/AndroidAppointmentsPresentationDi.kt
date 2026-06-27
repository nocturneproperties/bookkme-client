package me.bookk.feature.appointments.presentation.di

import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateViewModel
import me.bookk.feature.appointments.presentation.screen.details.AppointmentDetailsViewModel
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryViewModel
import me.bookk.feature.appointments.presentation.screen.list.AppointmentListViewModel
import me.bookk.feature.appointments.presentation.screen.request.AppointmentRequestViewModel
import me.bookk.feature.appointments.presentation.screen.settings.AppointmentSettingsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformAppointmentsDiModule(): Module = module {
    viewModelOf(::AppointmentListViewModel)
    viewModelOf(::AppointmentCreateViewModel)
    viewModelOf(::AppointmentDetailsViewModel)
    viewModelOf(::AppointmentSettingsViewModel)
    viewModelOf(::AppointmentHistoryViewModel)
    viewModelOf(::AppointmentRequestViewModel)
}