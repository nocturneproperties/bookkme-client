package me.bookk.feature.settings.domain.impl

import me.bookk.feature.settings.domain.api.DownloadPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import me.bookk.core.domain.entity.Error as DomainError

internal class DownloadPersonalDataExportImpl(
    private val dataSource: DataExportDataSource,
) : DownloadPersonalDataExport {
    override suspend fun invoke(): String {
        return runCatching { dataSource.download() }.getOrElse {
            if (it is DomainError.NotFound) {
                throw DownloadPersonalDataExport.Error.NotReady()
            }
            throw it
        }
    }
}
