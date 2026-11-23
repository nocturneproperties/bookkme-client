package me.bookk.feature.appointments.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.appointments.domain.api.GetSavedBusinesses
import me.bookk.feature.appointments.domain.api.SaveBusiness
import me.bookk.feature.appointments.domain.api.SaveBusiness.Error
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import kotlin.uuid.Uuid

internal class SaveBusinessImpl(
    private val dataSource: ClientBookingDataSource,
    private val getSavedBusinesses: GetSavedBusinesses,
) : SaveBusiness {

    override suspend fun invoke(businessId: Uuid) {
        runCatching { dataSource.saveBusiness(businessId) }
            .onBusinessError {
                when (it.errorCode) {
                    AppointmentErrorCodes.APPOINTMENTS_NOT_ENABLED -> {
                        throw Error.AppointmentsNotEnabled()
                    }

                    AppointmentErrorCodes.SAVED_BUSINESSES_LIMIT_REACHED -> {
                        throw Error.SavedBusinessesLimitReached()
                    }
                }
            }
            .getOrThrow()
        getSavedBusinesses.refresh()
    }
}
