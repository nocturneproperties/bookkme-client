package me.bookk.feature.settings.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.entity.DataExport
import okio.Path

interface DataExportDataSource {
    suspend fun start(confirmationToken: String): DataExport
    suspend fun get(): DataExport
    suspend fun download(): String
    fun observe(): Flow<DataExport?>
    suspend fun saveInDb(export: DataExport)
    suspend fun getFile(export: DataExport): Path?
    suspend fun saveFile(export: DataExport, content: String): Path
    suspend fun deleteFiles()
}
