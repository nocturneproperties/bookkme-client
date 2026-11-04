package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.settings.domain.api.DownloadPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

internal class DownloadPersonalDataExportImpl(private val dataSource: DataExportDataSource) :
    DownloadPersonalDataExport {
    override suspend fun invoke(): String {
        return runCatching { dataSource.download() }.getOrElse {
            if (it is DomainError.UnknownApiError &&
                it.errorCode == 404
            ) {
                throw DownloadPersonalDataExport.Error.NotReady()
            }
            throw it
        }
    }
}
