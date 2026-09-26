package me.bookk.feature.employees.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.feature.authorization.domain.api.UserProfileCRUD
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.employees.domain.api.IsBusinessOwner
import kotlin.uuid.Uuid

internal class IsBusinessOwnerImpl(
    private val observeUserBusinessesChanges: ObserveUserBusinessesChanges,
    private val userProfileCRUD: UserProfileCRUD
) : IsBusinessOwner {
    override suspend fun invoke(userId: Uuid, businessId: Uuid): Boolean {
        val business = observeUserBusinessesChanges().first().firstOrNull { it.id == businessId }
        return business?.ownerId == userId
    }

    override suspend fun invoke(businessId: Uuid): Boolean {
        return invoke(userProfileCRUD.get().id, businessId)
    }
}
