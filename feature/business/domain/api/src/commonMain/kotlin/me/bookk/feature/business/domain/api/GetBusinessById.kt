package me.bookk.feature.business.domain.api

import me.bookk.feature.business.domain.api.entity.Business

interface GetBusinessById {
    suspend fun invoke(id: Long): Business
}