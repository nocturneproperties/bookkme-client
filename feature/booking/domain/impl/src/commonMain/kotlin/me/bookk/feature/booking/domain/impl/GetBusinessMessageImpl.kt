package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.GetBookingInfo
import me.bookk.feature.booking.domain.api.GetBusinessMessage
import me.bookk.feature.booking.domain.api.GetBusinessMessage.Error
import kotlin.uuid.Uuid

internal class GetBusinessMessageImpl(
    private val getBookingInfo: GetBookingInfo,
) : GetBusinessMessage {

    override fun flow(businessId: Uuid): Flow<String?> {
        return getBookingInfo.flow(businessId)
            .map { info -> info?.appointmentNote?.trim()?.takeIf { it.isNotEmpty() } }
            .distinctUntilChanged()
    }

    override suspend fun refresh(businessId: Uuid) {
        try {
            getBookingInfo.refresh(businessId)
        } catch (error: GetBookingInfo.Error.AppointmentsNotEnabled) {
            throw Error.BookingUnavailable()
        }
    }
}
