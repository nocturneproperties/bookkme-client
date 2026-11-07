package me.bookk.feature.settings.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import kotlin.time.Clock

internal class GetPersonalDataExportImpl(
    private val dataSource: DataExportDataSource,
    private val clock: Clock,
) : GetPersonalDataExport {
    override fun flow(): Flow<DataExport?> {
        return dataSource.observe()
            .map { export -> export?.takeUnless { it.isExpiredAt(clock.now()) } }
    }

    override suspend fun refresh(): DataExport {
        val export = dataSource.get()
        if (!export.isDownloadableAt(clock.now())) {
            dataSource.deleteFiles()
        }
        dataSource.saveInDb(export)
        return export
    }
}
