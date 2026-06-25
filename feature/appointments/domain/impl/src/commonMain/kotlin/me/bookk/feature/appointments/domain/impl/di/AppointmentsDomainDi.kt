package me.bookk.feature.appointments.domain.impl.di

import me.bookk.feature.appointments.domain.api.CancelAppointment
import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.GetAppointmentHistory
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.GetAppointmentsForDashboardBusiness
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.UpdateAppointmentSettings
import me.bookk.feature.appointments.domain.impl.CancelAppointmentImpl
import me.bookk.feature.appointments.domain.impl.CreateAppointmentImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentHistoryImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentOptionsImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentSettingsImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentsForDashboardBusinessImpl
import me.bookk.feature.appointments.domain.impl.UpdateAppointmentImpl
import me.bookk.feature.appointments.domain.impl.UpdateAppointmentSettingsImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun appointmentsDomainModule() = module {
    factoryOf(::GetAppointmentsForDashboardBusinessImpl) bind GetAppointmentsForDashboardBusiness::class
    factoryOf(::GetAppointmentOptionsImpl) bind GetAppointmentOptions::class
    factoryOf(::GetAppointmentSettingsImpl) bind GetAppointmentSettings::class
    factoryOf(::CreateAppointmentImpl) bind CreateAppointment::class
    factoryOf(::GetAppointmentImpl) bind GetAppointment::class
    factoryOf(::GetAppointmentHistoryImpl) bind GetAppointmentHistory::class
    factoryOf(::CancelAppointmentImpl) bind CancelAppointment::class
    factoryOf(::UpdateAppointmentImpl) bind UpdateAppointment::class
    factoryOf(::UpdateAppointmentSettingsImpl) bind UpdateAppointmentSettings::class
}
