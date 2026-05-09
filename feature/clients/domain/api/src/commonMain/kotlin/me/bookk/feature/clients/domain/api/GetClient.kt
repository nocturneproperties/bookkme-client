package me.bookk.feature.clients.domain.api

import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.uuid.Uuid

interface GetClient {
    suspend operator fun invoke(id: Uuid): Client
}