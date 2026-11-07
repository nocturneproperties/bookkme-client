package me.bookk.feature.business.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.entity.DataExport
import kotlin.uuid.Uuid

interface GetBusinessDataExport {
    fun flow(businessId: Uuid): Flow<DataExport?>
    suspend fun refresh(businessId: Uuid): DataExport

    sealed interface Error {
        class NotOwner :
            Exception(),
            Error
    }
}
