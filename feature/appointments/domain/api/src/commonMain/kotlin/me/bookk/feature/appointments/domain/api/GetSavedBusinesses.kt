package me.bookk.feature.appointments.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.appointments.domain.api.entity.SavedBusiness

interface GetSavedBusinesses {
    fun flow(): Flow<List<SavedBusiness>>
    suspend fun refresh(): List<SavedBusiness>
}
