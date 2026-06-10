package me.bookk.feature.business.domain.api.plugin

import kotlin.uuid.Uuid

interface EnableAppointmentsPlugin {
    suspend operator fun invoke(businessId: Uuid)

    sealed interface Error {
        class AlreadyEnabled : Exception()
    }
}