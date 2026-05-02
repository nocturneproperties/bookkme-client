package me.bookk.feature.business.domain.api.client

import me.bookk.feature.business.domain.api.entity.Client
import kotlin.uuid.Uuid

interface GetClientsList {
    suspend operator fun invoke(businessId: Uuid): List<Client>
}