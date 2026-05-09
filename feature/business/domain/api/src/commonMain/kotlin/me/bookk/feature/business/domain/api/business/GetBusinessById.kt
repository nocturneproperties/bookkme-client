package me.bookk.feature.business.domain.api.business

import me.bookk.feature.business.domain.api.entity.Business
import kotlin.uuid.Uuid

interface GetBusinessById {
    suspend operator fun invoke(id: Uuid): Business
}