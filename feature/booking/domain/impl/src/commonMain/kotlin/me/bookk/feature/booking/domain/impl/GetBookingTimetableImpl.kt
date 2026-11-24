package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import me.bookk.feature.booking.domain.api.GetBookingTimetable
import me.bookk.feature.booking.domain.api.GetBookingTimetable.Error
import me.bookk.feature.booking.domain.api.entity.BookingTimetable
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import me.bookk.feature.booking.domain.datasource.BookingSelection
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.GetBookableEmployees
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import kotlin.uuid.Uuid

internal class GetBookingTimetableImpl(
    private val dataSource: BookingPreparationDataSource,
    private val getBusinesses: GetBusinesses,
    private val getBookableEmployees: GetBookableEmployees,
) : GetBookingTimetable {

    override fun flow(businessId: Uuid): Flow<BookingTimetable> {
        return combine(
            dataSource.observeSelection(businessId),
            getBusinesses.flow(setOf(businessId)),
            getBookableEmployees.flow(businessId),
            ::timetable,
        )
            .filterNotNull()
            .distinctUntilChanged()
    }

    override suspend fun refresh(businessId: Uuid) {
        coroutineScope {
            val employees = async { getBookableEmployees.refresh(businessId) }
            val businesses = getBusinesses.refresh(setOf(businessId))
            employees.await()
            if (businesses.isEmpty()) throw Error.BusinessNotFound()
        }
    }

    private fun timetable(
        selection: BookingSelection,
        businesses: List<Business>,
        employees: List<BookableEmployee>,
    ): BookingTimetable? {
        val business = businesses.firstOrNull() ?: return null
        return BookingTimetable(
            businessName = business.name,
            timeZone = business.timeZone,
            specialist = employees
                .firstOrNull { it.id == selection.specialistId }
                ?.toBookingSpecialist(),
            closedWeekdays = business.schedule.closedWeekdays(),
            daysOff = business.schedule.daysOff(),
        )
    }

    private fun WorkingSchedule.closedWeekdays(): Set<DayOfWeek> {
        return DayOfWeek.entries
            .filter { days[it]?.isActive != true }
            .toSet()
    }

    private fun WorkingSchedule.daysOff(): List<ClosedRange<LocalDate>> {
        return dayOffs.map { it.start..it.end }
    }
}
