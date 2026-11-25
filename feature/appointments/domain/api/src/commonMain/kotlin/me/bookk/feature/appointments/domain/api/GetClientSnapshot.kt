package me.bookk.feature.appointments.domain.api

import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import kotlin.uuid.Uuid

interface GetClientSnapshot {
    suspend operator fun invoke(clientId: Uuid): ClientSnapshot
}
