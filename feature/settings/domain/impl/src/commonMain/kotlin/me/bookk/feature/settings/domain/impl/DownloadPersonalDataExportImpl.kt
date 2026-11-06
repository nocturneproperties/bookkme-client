package me.bookk.feature.settings.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.core.domain.entity.onNotFound
import me.bookk.feature.settings.domain.api.DownloadPersonalDataExport
import me.bookk.feature.settings.domain.api.DownloadPersonalDataExport.Error
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import okio.Path

internal class DownloadPersonalDataExportImpl(
    private val getPersonalDataExport: GetPersonalDataExport,
    private val dataSource: DataExportDataSource,
) : DownloadPersonalDataExport {
    override suspend fun invoke(): Path {
        val export = getPersonalDataExport.flow().first()
            ?.takeIf { it.status == DataExportStatus.READY }
            ?: throw Error.NotReady()
        return dataSource.getFile(export) ?: download(export)
    }

    private suspend fun download(export: DataExport): Path {
        val content = runCatching { dataSource.download() }
            .onNotFound { throw Error.NotReady() }
            .getOrThrow()
        return dataSource.saveFile(export, content)
    }
}
