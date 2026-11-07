package me.bookk.feature.business.domain.impl

import me.bookk.feature.business.domain.api.DeleteExpiredBusinessDataExportFiles
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.time.Clock

internal class DeleteExpiredBusinessDataExportFilesImpl(
    private val dataSource: DataExportDataSource,
    private val clock: Clock,
) : DeleteExpiredBusinessDataExportFiles {
    override suspend fun invoke() {
        val now = clock.now()
        dataSource.getAllFromDb()
            .filterValues { !it.isDownloadableAt(now) }
            .keys
            .forEach { dataSource.deleteFiles(it) }
    }
}
