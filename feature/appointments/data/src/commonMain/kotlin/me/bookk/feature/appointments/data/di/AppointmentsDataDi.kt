package me.bookk.feature.appointments.data.di

import me.bookk.feature.appointments.data.datasource.CommonAppointmentDataSource
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun appointmentsDataModule() = module {
    singleOf(::CommonAppointmentDataSource) bind AppointmentDataSource::class
}