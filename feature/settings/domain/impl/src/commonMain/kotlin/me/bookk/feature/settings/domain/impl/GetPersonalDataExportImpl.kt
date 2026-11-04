package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.settings.domain.api.GetPersonalDataExport
import me.bookk.feature.settings.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid

internal class GetPersonalDataExportImpl(
    private val dataSource: DataExportDataSource
) : GetPersonalDataExport {
    override suspend fun invoke(): DataExport {
        return dataSource.get()
    }
}
