package me.bookk.feature.clients.domain.api

import me.bookk.feature.clients.domain.api.entity.ClientsPermissions
import kotlin.uuid.Uuid

interface GetClientsPermissions {
    suspend operator fun invoke(businessId: Uuid): ClientsPermissions
}
