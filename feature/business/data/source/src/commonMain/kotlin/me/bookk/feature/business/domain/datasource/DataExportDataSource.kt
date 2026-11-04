package me.bookk.feature.business.domain.datasource

import me.bookk.core.domain.entity.DataExport
import kotlin.uuid.Uuid

interface DataExportDataSource {
    suspend fun start(businessId: Uuid, stepUpToken: String): DataExport
    suspend fun get(businessId: Uuid): DataExport
    suspend fun download(businessId: Uuid): String
}
