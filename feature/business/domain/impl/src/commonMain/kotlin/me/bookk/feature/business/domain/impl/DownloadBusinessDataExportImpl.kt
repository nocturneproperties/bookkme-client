package me.bookk.feature.business.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.domain.entity.onNotFound
import me.bookk.feature.business.domain.api.DownloadBusinessDataExport
import me.bookk.feature.business.domain.api.DownloadBusinessDataExport.Error
import me.bookk.feature.business.domain.api.GetBusinessDataExport
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import okio.Path
import kotlin.uuid.Uuid

internal class DownloadBusinessDataExportImpl(
    private val getBusinessDataExport: GetBusinessDataExport,
    private val dataSource: DataExportDataSource,
) : DownloadBusinessDataExport {
    override suspend fun invoke(businessId: Uuid): Path {
        val export = getBusinessDataExport.flow(businessId).first()
            ?.takeIf { it.status == DataExportStatus.READY }
            ?: throw Error.NotReady()
        return dataSource.getFile(businessId, export) ?: download(businessId, export)
    }

    private suspend fun download(businessId: Uuid, export: DataExport): Path {
        val content = runCatching { dataSource.download(businessId) }
            .onNotFound { throw Error.NotReady() }
            .getOrThrow()
        return dataSource.saveFile(businessId, export, content)
    }
}
