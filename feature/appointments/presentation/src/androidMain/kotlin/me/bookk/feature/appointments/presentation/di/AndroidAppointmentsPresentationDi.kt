package me.bookk.feature.appointments.presentation.di

import me.bookk.feature.appointments.presentation.screen.create.AppointmentCreateViewModel
import me.bookk.feature.appointments.presentation.screen.requestlist.AppointmentListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal actual fun platformAppointmentsDiModule(): Module = module {
    viewModelOf(::AppointmentListViewModel)
    viewModelOf(::AppointmentCreateViewModel)
}