package me.bookk.feature.appointments.data.di

import me.bookk.feature.appointments.data.datasource.CommonAppointmentDataSource
import me.bookk.feature.appointments.data.datasource.CommonAppointmentRequestDataSource
import me.bookk.feature.appointments.data.datasource.CommonAppointmentSettingsDataSource
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import me.bookk.feature.appointments.domain.datasource.AppointmentSettingsDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun appointmentsDataModule() = module {
    singleOf(::CommonAppointmentDataSource) bind AppointmentDataSource::class
    singleOf(::CommonAppointmentRequestDataSource) bind AppointmentRequestDataSource::class
    singleOf(::CommonAppointmentSettingsDataSource) bind AppointmentSettingsDataSource::class
}