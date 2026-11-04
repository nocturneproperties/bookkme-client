package me.bookk.feature.settings.domain.datasource

import me.bookk.core.domain.entity.DataExport
import kotlin.uuid.Uuid

interface DataExportDataSource {
    suspend fun start(stepUpToken: String): DataExport
    suspend fun get(): DataExport
    suspend fun download(): String
}
