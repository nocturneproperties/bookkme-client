package me.bookk.feature.business.domain.datasource

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.entity.DataExport
import okio.Path
import kotlin.uuid.Uuid

interface DataExportDataSource {
    suspend fun start(businessId: Uuid, confirmationToken: String): DataExport
    suspend fun get(businessId: Uuid): DataExport
    suspend fun download(businessId: Uuid): String
    fun observe(businessId: Uuid): Flow<DataExport?>
    suspend fun getAllFromDb(): Map<Uuid, DataExport>
    suspend fun saveInDb(businessId: Uuid, export: DataExport)
    suspend fun deleteFromDb(businessId: Uuid)
    suspend fun getFile(businessId: Uuid, export: DataExport): Path?
    suspend fun saveFile(businessId: Uuid, export: DataExport, content: String): Path
    suspend fun deleteFiles(businessId: Uuid)
}
