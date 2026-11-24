package me.bookk.feature.booking.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import me.bookk.feature.booking.domain.api.GetBookingSpecialists
import me.bookk.feature.booking.domain.api.entity.BookingSpecialist
import me.bookk.feature.booking.domain.datasource.BookingPreparationDataSource
import me.bookk.feature.booking.domain.datasource.BookingSelection
import me.bookk.feature.employees.domain.api.GetBookableEmployees
import me.bookk.feature.employees.domain.api.entity.BookableEmployee
import kotlin.uuid.Uuid

internal class GetBookingSpecialistsImpl(
    private val dataSource: BookingPreparationDataSource,
    private val getBookableEmployees: GetBookableEmployees,
) : GetBookingSpecialists {

    override fun flow(businessId: Uuid): Flow<List<BookingSpecialist>> {
        return combine(
            dataSource.observeSelection(businessId),
            getBookableEmployees.flow(businessId),
            ::specialists,
        ).distinctUntilChanged()
    }

    override suspend fun refresh(businessId: Uuid): List<BookingSpecialist> {
        val employees = getBookableEmployees.refresh(businessId)
        return specialists(dataSource.observeSelection(businessId).first(), employees)
    }

    private fun specialists(
        selection: BookingSelection,
        employees: List<BookableEmployee>,
    ): List<BookingSpecialist> {
        val picked = selection.services.map { it.serviceId }
        return employees
            .filter { employee -> employee.services.map { it.id }.containsAll(picked) }
            .map { it.toBookingSpecialist() }
    }
}
