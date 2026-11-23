package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.GetSavedBusinesses
import me.bookk.feature.appointments.domain.api.entity.SavedBusiness
import me.bookk.feature.appointments.domain.api.entity.SavedBusinessEntry
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.business.domain.api.business.GetBusinesses
import kotlin.uuid.Uuid

internal class GetSavedBusinessesImpl(
    private val dataSource: ClientBookingDataSource,
    private val getBusinesses: GetBusinesses,
) : GetSavedBusinesses {

    override fun flow(): Flow<List<SavedBusiness>> {
        return dataSource.observeSavedBusinesses().flatMapLatest { entries ->
            getBusinesses.flow(entries.businessIds())
                .map { businesses -> entries.toSavedBusinesses(businesses.names()) }
        }
    }

    override suspend fun refresh(): List<SavedBusiness> {
        val entries = dataSource.getSavedBusinesses()
        val businesses = getBusinesses.refresh(entries.businessIds())
        dataSource.saveSavedBusinessesInDb(entries)
        return entries.toSavedBusinesses(businesses.names())
    }

    private fun List<SavedBusinessEntry>.businessIds(): Set<Uuid> {
        return map { it.businessId }.toSet()
    }

    private fun List<SavedBusinessEntry>.toSavedBusinesses(
        names: Map<Uuid, String>,
    ): List<SavedBusiness> {
        return map {
            SavedBusiness(
                id = it.businessId,
                name = names[it.businessId].orEmpty(),
                isEnabled = it.isEnabled,
                savedAt = it.savedAt,
                lastVisitAt = it.lastVisitAt,
            )
        }
    }
}
