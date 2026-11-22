package me.bookk.feature.business.domain.api.link

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.BusinessLink
import kotlin.uuid.Uuid

interface GetBusinessLink {
    fun flow(businessId: Uuid): Flow<BusinessLink?>
    suspend fun refresh(businessId: Uuid)

    sealed interface Error {
        class NotOwner :
            Exception(),
            Error
    }
}
