package me.bookk.feature.appointments.domain.impl.di

import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.GetAppointmentsForDashboardBusiness
import me.bookk.feature.appointments.domain.impl.CreateAppointmentImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentOptionsImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentSettingsImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentsForDashboardBusinessImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun appointmentsDomainModule() = module {
    factoryOf(::GetAppointmentsForDashboardBusinessImpl) bind GetAppointmentsForDashboardBusiness::class
    factoryOf(::GetAppointmentOptionsImpl) bind GetAppointmentOptions::class
    factoryOf(::GetAppointmentSettingsImpl) bind GetAppointmentSettings::class
    factoryOf(::CreateAppointmentImpl) bind CreateAppointment::class
}
