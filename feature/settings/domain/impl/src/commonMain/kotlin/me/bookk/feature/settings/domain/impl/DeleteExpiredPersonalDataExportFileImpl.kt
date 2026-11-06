package me.bookk.feature.settings.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.feature.settings.domain.api.DeleteExpiredPersonalDataExportFile
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import kotlin.time.Clock

internal class DeleteExpiredPersonalDataExportFileImpl(
    private val dataSource: DataExportDataSource,
    private val clock: Clock,
) : DeleteExpiredPersonalDataExportFile {
    override suspend fun invoke() {
        val export = dataSource.observe().first()
        if (export?.isDownloadableAt(clock.now()) != true) {
            dataSource.deleteFiles()
        }
    }
}
