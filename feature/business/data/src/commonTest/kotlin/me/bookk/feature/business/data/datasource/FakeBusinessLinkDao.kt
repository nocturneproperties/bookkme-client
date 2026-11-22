package me.bookk.feature.business.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import me.bookk.database.dao.BusinessLinkDao
import me.bookk.database.entity.BusinessLinkEntity
import kotlin.uuid.Uuid

internal class FakeBusinessLinkDao : BusinessLinkDao() {
    private val links = MutableStateFlow<Map<Uuid, BusinessLinkEntity>>(emptyMap())

    override fun observe(businessId: Uuid): Flow<BusinessLinkEntity?> {
        return links.map { it[businessId] }
    }

    override suspend fun upsert(link: BusinessLinkEntity) {
        links.value += link.businessId to link
    }

    override suspend fun delete(businessId: Uuid) {
        links.value -= businessId
    }

    override suspend fun clear() {
        links.value = emptyMap()
    }
}
