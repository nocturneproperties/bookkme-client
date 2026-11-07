package me.bookk.feature.business.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import me.bookk.database.dao.BusinessDataExportDao
import me.bookk.database.entity.BusinessDataExportEntity
import kotlin.uuid.Uuid

internal class FakeBusinessDataExportDao : BusinessDataExportDao() {
    private val snapshots = MutableSharedFlow<Map<Uuid, BusinessDataExportEntity>>(
        replay = 1,
        extraBufferCapacity = 1,
    ).apply { tryEmit(emptyMap()) }
    val exports: Map<Uuid, BusinessDataExportEntity>
        get() {
            return snapshots.replayCache.last()
        }

    override fun observe(businessId: Uuid): Flow<BusinessDataExportEntity?> {
        return snapshots.map { it[businessId] }
    }

    override suspend fun getAll(): List<BusinessDataExportEntity> {
        return exports.values.toList()
    }

    override suspend fun upsert(export: BusinessDataExportEntity) {
        check(snapshots.tryEmit(exports + (export.businessId to export)))
    }

    override suspend fun delete(businessId: Uuid) {
        check(snapshots.tryEmit(exports - businessId))
    }

    override suspend fun clear() {
        check(snapshots.tryEmit(emptyMap()))
    }
}
