package me.bookk.feature.services.domain.api.service

import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

interface GetServices {
    suspend operator fun invoke(businessId: Uuid): List<Service>
    suspend fun cached(businessId: Uuid, onResultAvailable: suspend (List<Service>) -> Unit)
}