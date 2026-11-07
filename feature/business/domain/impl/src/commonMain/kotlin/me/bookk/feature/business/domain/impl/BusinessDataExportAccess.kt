package me.bookk.feature.business.domain.impl

import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid

internal suspend fun DataExportDataSource.forgetExport(businessId: Uuid) {
    deleteFiles(businessId)
    deleteFromDb(businessId)
}
