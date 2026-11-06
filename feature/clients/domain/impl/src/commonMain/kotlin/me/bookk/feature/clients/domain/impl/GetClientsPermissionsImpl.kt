package me.bookk.feature.clients.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.clients.domain.api.GetClientsPermissions
import me.bookk.feature.clients.domain.api.entity.ClientsPermissions
import kotlin.uuid.Uuid

internal class GetClientsPermissionsImpl(
    private val observeUserBusinessesChanges: ObserveUserBusinessesChanges,
    private val userProfileCRUD: UserProfileCRUD,
) : GetClientsPermissions {
    override suspend fun invoke(businessId: Uuid): ClientsPermissions {
        val business = observeUserBusinessesChanges().first()
            .firstOrNull { it.id == businessId }
        val clients = business?.permissions?.clients
        return ClientsPermissions(
            canEdit = clients?.update == true,
            canDeleteDetached = clients?.delete == true,
            canDeleteIntegrated = business?.ownerId != null &&
                business.ownerId == userProfileCRUD.observe().first()?.id,
        )
    }
}
