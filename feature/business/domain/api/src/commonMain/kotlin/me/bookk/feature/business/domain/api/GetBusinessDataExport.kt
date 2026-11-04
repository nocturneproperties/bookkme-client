package me.bookk.feature.business.domain.api

import me.bookk.core.domain.entity.DataExport
import kotlin.uuid.Uuid

interface GetBusinessDataExport {
    suspend operator fun invoke(businessId: Uuid): DataExport
}
