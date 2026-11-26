package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDateTime
import me.bookk.core.coroutine.runSuspendCatching
import me.bookk.feature.appointments.domain.api.GetFreeSlots
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsDay
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.booking.domain.api.GetBookingSlots
import me.bookk.feature.booking.domain.api.GetBookingSlots.Error
import me.bookk.feature.booking.domain.api.entity.BookingDay
import me.bookk.feature.booking.domain.api.entity.BookingSlot
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import me.bookk.feature.booking.domain.datasource.BookingSelection
import kotlin.uuid.Uuid

internal class GetBookingSlotsImpl(
    private val dataSource: BookingPreparationDataSource,
    private val getFreeSlots: GetFreeSlots,
) : GetBookingSlots {

    override suspend fun invoke(
        businessId: Uuid,
        timetable: BookingTimetable,
        from: LocalDate,
        to: LocalDate,
    ): List<BookingDay> {
        val selection = dataSource.observeSelection(businessId).first()
        return freeSlots(query(businessId, selection, from, to))
            .map { it.toBookingDay(timetable) }
    }

    private fun query(
        businessId: Uuid,
        selection: BookingSelection,
        from: LocalDate,
        to: LocalDate,
    ): FreeSlotsQuery {
        val specialistId = selection.specialistId
        if (specialistId == null || selection.services.isEmpty()) throw Error.SelectionInvalid()
        return FreeSlotsQuery(
            businessId = businessId,
            employeeId = specialistId,
            services = selection.services.map { RequestedService(it.serviceId, it.quantity) },
            from = from,
            to = to,
        )
    }

    private suspend fun freeSlots(query: FreeSlotsQuery): List<FreeSlotsDay> {
        return runSuspendCatching { getFreeSlots(query) }
            .getOrElse { throw it.toBookingError() }
    }

    private fun Throwable.toBookingError(): Throwable {
        return when (this) {
            is GetFreeSlots.Error.AppointmentsNotEnabled -> Error.BookingUnavailable()
            is GetFreeSlots.Error.InvalidServiceSelection -> Error.SelectionInvalid()
            is GetFreeSlots.Error.InvalidRange -> Error.InvalidRange()
            else -> this
        }
    }

    private fun FreeSlotsDay.toBookingDay(timetable: BookingTimetable): BookingDay {
        return BookingDay(
            date = date,
            slots = slots.map {
                BookingSlot(it.start, it.start.toLocalDateTime(timetable.timeZone).time)
            },
            isBusinessClosed = timetable.isClosedOn(date),
        )
    }
}
