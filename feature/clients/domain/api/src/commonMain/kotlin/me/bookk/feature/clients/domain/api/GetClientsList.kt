package me.bookk.feature.clients.domain.api

import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.uuid.Uuid

interface GetClientsList {
    suspend operator fun invoke(businessId: Uuid): List<Client>
}