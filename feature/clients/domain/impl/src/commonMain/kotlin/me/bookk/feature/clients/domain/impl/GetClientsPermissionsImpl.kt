package me.bookk.feature.clients.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import me.bookk.feature.clients.domain.api.entity.ClientsPermissions
import kotlin.uuid.Uuid

internal class GetClientsPermissionsImpl(
    private val observeUserBusinessesChanges: ObserveUserBusinessesChanges
) : GetClientsPermissions {
    override suspend fun invoke(businessId: Uuid): ClientsPermissions {
        val clients = observeUserBusinessesChanges().first()
            .firstOrNull { it.id == businessId }
            ?.permissions
            ?.clients
        return ClientsPermissions(
            canEdit = clients?.update == true,
            canDelete = clients?.delete == true
        )
    }
}
