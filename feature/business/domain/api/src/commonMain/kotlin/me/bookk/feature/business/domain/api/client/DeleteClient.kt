package me.bookk.feature.business.domain.api.client

import me.bookk.feature.business.domain.api.entity.Client

interface DeleteClient {
    suspend operator fun invoke(client: Client)
}