package me.bookk.feature.business.domain.api.client

import me.bookk.feature.business.domain.api.entity.Client

interface CreateClient {
    suspend operator fun invoke(client: Client): Client
}