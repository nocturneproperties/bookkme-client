package me.bookk.feature.services.domain.api

import me.bookk.feature.services.domain.api.entity.ServicesPermissions
import kotlin.uuid.Uuid

interface GetServicesPermissions {
    suspend operator fun invoke(businessId: Uuid): ServicesPermissions
}
