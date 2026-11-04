package me.bookk.feature.business.domain.impl

import me.bookk.core.domain.entity.DataExport
import me.bookk.feature.business.domain.api.GetBusinessDataExport
import me.bookk.feature.business.domain.datasource.DataExportDataSource
import kotlin.uuid.Uuid

internal class GetBusinessDataExportImpl(private val dataSource: DataExportDataSource) :
    GetBusinessDataExport {
    override suspend fun invoke(businessId: Uuid): DataExport {
        return dataSource.get(businessId)
    }
}
