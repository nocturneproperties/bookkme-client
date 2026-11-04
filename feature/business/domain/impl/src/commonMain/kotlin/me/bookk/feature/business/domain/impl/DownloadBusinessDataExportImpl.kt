package me.bookk.feature.business.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.business.domain.api.DownloadBusinessDataExport
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

internal class DownloadBusinessDataExportImpl(
    private val dataSource: DataExportDataSource
) : DownloadBusinessDataExport {
    override suspend fun invoke(businessId: Uuid): String {
        return runCatching { dataSource.download(businessId) }.getOrElse {
            if (it is DomainError.UnknownApiError && it.errorCode == 404) throw DownloadBusinessDataExport.Error.NotReady()
            throw it
        }
    }
}
