package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.GetFreeSlots
import me.bookk.feature.appointments.domain.api.GetFreeSlots.Error
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsDay
import me.bookk.feature.appointments.domain.api.entity.FreeSlotsQuery
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource

internal class GetFreeSlotsImpl(
    private val dataSource: ClientBookingDataSource,
) : GetFreeSlots {

    override suspend fun invoke(query: FreeSlotsQuery): List<FreeSlotsDay> {
        return runCatching { dataSource.getFreeSlots(query) }
            .onBusinessError {
                when (it.errorCode) {
                    AppointmentErrorCodes.APPOINTMENTS_NOT_ENABLED -> {
                        throw Error.AppointmentsNotEnabled()
                    }

                    AppointmentErrorCodes.SERVICE_SELECTION_INVALID -> {
                        throw Error.InvalidServiceSelection()
                    }

                    AppointmentErrorCodes.SLOT_RANGE_INVALID -> {
                        throw Error.InvalidRange()
                    }
                }
            }
            .getOrThrow()
    }
}
