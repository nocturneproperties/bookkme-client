package me.bookk.core.domain.fetch

import kotlin.uuid.Uuid

interface BusinessDataFetchAction {
    suspend fun doOnBusinessDataFetch(businessId: Uuid)
}
