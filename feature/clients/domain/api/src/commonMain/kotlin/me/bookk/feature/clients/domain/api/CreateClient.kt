package me.bookk.feature.clients.domain.api

import me.bookk.feature.clients.domain.api.entity.Client

interface CreateClient {
    suspend operator fun invoke(client: Client): Client
}