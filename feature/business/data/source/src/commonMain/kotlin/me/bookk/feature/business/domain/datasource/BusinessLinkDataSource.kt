package me.bookk.feature.business.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.business.domain.api.entity.BusinessLink
import kotlin.uuid.Uuid

interface BusinessLinkDataSource {
    suspend fun getLink(businessId: Uuid): BusinessLink
    suspend fun setLink(businessId: Uuid, name: String): BusinessLink
    fun observeLink(businessId: Uuid): Flow<BusinessLink?>
    suspend fun saveLinkInDb(link: BusinessLink)
    suspend fun deleteLinkFromDb(businessId: Uuid)
}
