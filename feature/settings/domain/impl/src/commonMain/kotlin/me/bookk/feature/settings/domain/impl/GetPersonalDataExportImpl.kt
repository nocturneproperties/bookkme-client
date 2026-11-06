package me.bookk.feature.settings.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.domain.entity.DataExport
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import kotlin.time.Clock

internal class GetPersonalDataExportImpl(
    private val dataSource: DataExportDataSource,
    private val clock: Clock,
) : GetPersonalDataExport {
    override fun flow(): Flow<DataExport?> {
        return dataSource.observe()
            .map { export -> export?.takeUnless { it.isExpired() } }
    }

    override suspend fun refresh(): DataExport {
        val export = dataSource.get()
        if (!export.isDownloadable()) {
            dataSource.deleteFiles()
        }
        dataSource.saveInDb(export)
        return export
    }

    private fun DataExport.isExpired(): Boolean {
        return expiresAt?.let { it <= clock.now() } == true
    }

    private fun DataExport.isDownloadable(): Boolean {
        return status == DataExportStatus.READY && !isExpired()
    }
}
