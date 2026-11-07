package me.bookk.feature.business.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.onNotFound
import me.bookk.feature.business.domain.api.GetBusinessDataExport
import me.bookk.feature.business.domain.api.GetBusinessDataExport.Error
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.time.Clock
import kotlin.uuid.Uuid

internal class GetBusinessDataExportImpl(
    private val dataSource: DataExportDataSource,
    private val clock: Clock,
) : GetBusinessDataExport {
    override fun flow(businessId: Uuid): Flow<DataExport?> {
        return dataSource.observe(businessId)
            .map { export -> export?.takeUnless { it.isExpiredAt(clock.now()) } }
    }

    override suspend fun refresh(businessId: Uuid): DataExport {
        val export = getOwnedExport(businessId)
        if (!export.isDownloadableAt(clock.now())) {
            dataSource.deleteFiles(businessId)
        }
        dataSource.saveInDb(businessId, export)
        return export
    }

    private suspend fun getOwnedExport(businessId: Uuid): DataExport {
        return runCatching { dataSource.get(businessId) }
            .onNotFound {
                dataSource.forgetExport(businessId)
                throw Error.NotOwner()
            }
            .getOrThrow()
    }
}
