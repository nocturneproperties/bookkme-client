package me.bookk.feature.services.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.services.domain.api.GetServicesPermissions
import me.bookk.feature.services.domain.api.entity.ServicesPermissions
import kotlin.uuid.Uuid

internal class GetServicesPermissionsImpl(
    private val observeUserBusinessesChanges: ObserveUserBusinessesChanges
) : GetServicesPermissions {
    override suspend fun invoke(businessId: Uuid): ServicesPermissions {
        val services = observeUserBusinessesChanges().first()
            .firstOrNull { it.id == businessId }
            ?.permissions
            ?.services
        return ServicesPermissions(
            canEdit = services?.update == true,
            canDelete = services?.delete == true
        )
    }
}
