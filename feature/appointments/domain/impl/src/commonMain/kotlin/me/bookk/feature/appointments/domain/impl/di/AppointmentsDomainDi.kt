package me.bookk.feature.appointments.domain.impl.di

import me.bookk.core.domain.fetch.BusinessDataFetchAction
import me.bookk.feature.appointments.domain.api.ApproveAppointmentRequest
import me.bookk.feature.appointments.domain.api.CancelAppointment
import me.bookk.feature.appointments.domain.api.CompleteAppointment
import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.CreateAppointmentRequest
import me.bookk.feature.appointments.domain.api.DeclineAppointmentRequest
import me.bookk.feature.appointments.domain.api.GetAppointment
import me.bookk.feature.appointments.domain.api.GetAppointmentHistory
import me.bookk.feature.appointments.domain.api.GetAppointmentOptions
import me.bookk.feature.appointments.domain.api.GetAppointmentSettings
import me.bookk.feature.appointments.domain.api.GetAppointmentsForBusiness
import me.bookk.feature.appointments.domain.api.GetAssignedAppointmentRequests
import me.bookk.feature.appointments.domain.api.GetBookingInfo
import me.bookk.feature.appointments.domain.api.GetBusinessAppointmentRequests
import me.bookk.feature.appointments.domain.api.GetClientAppointment
import me.bookk.feature.appointments.domain.api.GetClientAppointmentRequest
import me.bookk.feature.appointments.domain.api.GetFreeSlots
import me.bookk.feature.appointments.domain.api.GetSavedBusinesses
import me.bookk.feature.appointments.domain.api.GetUpcomingBookings
import me.bookk.feature.appointments.domain.api.MarkAppointmentNoShow
import me.bookk.feature.appointments.domain.api.ObserveAppointmentOptions
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusiness
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.appointments.domain.api.SaveBusiness
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.UpdateAppointmentSettings
import me.bookk.feature.appointments.domain.api.WithdrawAppointmentRequest
import me.bookk.feature.appointments.domain.impl.ApproveAppointmentRequestImpl
import me.bookk.feature.appointments.domain.impl.CancelAppointmentImpl
import me.bookk.feature.appointments.domain.impl.CompleteAppointmentImpl
import me.bookk.feature.appointments.domain.impl.CreateAppointmentImpl
import me.bookk.feature.appointments.domain.impl.CreateAppointmentRequestImpl
import me.bookk.feature.appointments.domain.impl.DeclineAppointmentRequestImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentHistoryImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentOptionsImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentSettingsImpl
import me.bookk.feature.appointments.domain.impl.GetAppointmentsForBusinessImpl
import me.bookk.feature.appointments.domain.impl.GetAssignedAppointmentRequestsImpl
import me.bookk.feature.appointments.domain.impl.GetBookingInfoImpl
import me.bookk.feature.appointments.domain.impl.GetBusinessAppointmentRequestsImpl
import me.bookk.feature.appointments.domain.impl.GetClientAppointmentImpl
import me.bookk.feature.appointments.domain.impl.GetClientAppointmentRequestImpl
import me.bookk.feature.appointments.domain.impl.GetFreeSlotsImpl
import me.bookk.feature.appointments.domain.impl.GetSavedBusinessesImpl
import me.bookk.feature.appointments.domain.impl.GetUpcomingBookingsImpl
import me.bookk.feature.appointments.domain.impl.MarkAppointmentNoShowImpl
import me.bookk.feature.appointments.domain.impl.ObserveAppointmentOptionsImpl
import me.bookk.feature.appointments.domain.impl.ObserveCurrentBusinessIdImpl
import me.bookk.feature.appointments.domain.impl.ObserveCurrentBusinessImpl
import me.bookk.feature.appointments.domain.impl.SaveBusinessImpl
import me.bookk.feature.appointments.domain.impl.UpdateAppointmentImpl
import me.bookk.feature.appointments.domain.impl.UpdateAppointmentSettingsImpl
import me.bookk.feature.appointments.domain.impl.WithdrawAppointmentRequestImpl
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.binds
import org.koin.dsl.module

fun appointmentsDomainModule() = module {
    factoryOf(::GetBusinessAppointmentRequestsImpl) bind GetBusinessAppointmentRequests::class
    factoryOf(::GetAppointmentOptionsImpl) bind GetAppointmentOptions::class
    factoryOf(::ObserveAppointmentOptionsImpl) bind ObserveAppointmentOptions::class
    factoryOf(::GetAppointmentSettingsImpl) binds
        arrayOf(GetAppointmentSettings::class, BusinessDataFetchAction::class)
    factoryOf(::CreateAppointmentImpl) bind CreateAppointment::class
    factoryOf(::CreateAppointmentRequestImpl) bind CreateAppointmentRequest::class
    factoryOf(::GetAppointmentImpl) bind GetAppointment::class
    factoryOf(::GetAppointmentHistoryImpl) bind GetAppointmentHistory::class
    factoryOf(::CancelAppointmentImpl) bind CancelAppointment::class
    factoryOf(::CompleteAppointmentImpl) bind CompleteAppointment::class
    factoryOf(::MarkAppointmentNoShowImpl) bind MarkAppointmentNoShow::class
    factoryOf(::UpdateAppointmentImpl) bind UpdateAppointment::class
    factoryOf(::UpdateAppointmentSettingsImpl) bind UpdateAppointmentSettings::class
    factoryOf(::GetAssignedAppointmentRequestsImpl) bind GetAssignedAppointmentRequests::class
    factoryOf(::ApproveAppointmentRequestImpl) bind ApproveAppointmentRequest::class
    factoryOf(::DeclineAppointmentRequestImpl) bind DeclineAppointmentRequest::class
    factoryOf(::ObserveCurrentBusinessIdImpl) bind ObserveCurrentBusinessId::class
    factoryOf(::ObserveCurrentBusinessImpl) bind ObserveCurrentBusiness::class
    factoryOf(::GetAppointmentsForBusinessImpl) bind GetAppointmentsForBusiness::class
}

fun clientAppointmentsDomainModule() = module {
    factoryOf(::GetUpcomingBookingsImpl) bind GetUpcomingBookings::class
    factoryOf(::GetSavedBusinessesImpl) bind GetSavedBusinesses::class
    factoryOf(::SaveBusinessImpl) bind SaveBusiness::class
    factoryOf(::GetFreeSlotsImpl) bind GetFreeSlots::class
    factoryOf(::CreateAppointmentRequestImpl) bind CreateAppointmentRequest::class
    factoryOf(::GetBookingInfoImpl) bind GetBookingInfo::class
    factoryOf(::WithdrawAppointmentRequestImpl) bind WithdrawAppointmentRequest::class
    factoryOf(::GetClientAppointmentRequestImpl) bind GetClientAppointmentRequest::class
    factoryOf(::GetClientAppointmentImpl) bind GetClientAppointment::class
}
