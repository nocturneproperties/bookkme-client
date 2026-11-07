package me.bookk.feature.settings.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.core.domain.entity.DataExport

interface GetPersonalDataExport {
    fun flow(): Flow<DataExport?>
    suspend fun refresh(): DataExport
}
