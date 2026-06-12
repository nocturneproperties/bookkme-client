package me.bookk.feature.appointments.domain.impl.di

import me.bookk.feature.appointments.domain.api.GetAppointmentsForDashboardBusiness
import me.bookk.feature.appointments.domain.impl.GetAppointmentsForDashboardBusinessImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun appointmentsDomainModule() = module {
    factoryOf(::GetAppointmentsForDashboardBusinessImpl) bind GetAppointmentsForDashboardBusiness::class
}
