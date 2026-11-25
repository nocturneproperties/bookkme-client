package me.bookk.feature.appointments.domain.impl

import me.bookk.feature.appointments.domain.api.GetClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.clients.domain.api.GetClient
import kotlin.uuid.Uuid

internal class GetClientSnapshotImpl(
    private val getClient: GetClient,
) : GetClientSnapshot {
    override suspend fun invoke(clientId: Uuid): ClientSnapshot {
        return getClient(clientId).snapshot()
    }
}
