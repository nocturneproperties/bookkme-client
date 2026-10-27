package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.AppointmentRequestEntity
import me.bookk.database.entity.AppointmentRequestServiceSnapshotEntity
import me.bookk.database.relation.AppointmentRequestLocal
import kotlin.uuid.Uuid

@Dao
abstract class AppointmentRequestDao {

    @Transaction
    @Query("SELECT * FROM appointment_request WHERE businessId = :businessId")
    abstract fun observeForBusiness(businessId: Uuid): Flow<List<AppointmentRequestLocal>>

    @Query("SELECT id FROM appointment_request WHERE businessId = :businessId")
    abstract suspend fun getIdsForBusiness(businessId: Uuid): List<Uuid>

    @Query("DELETE FROM appointment_request WHERE id IN (:ids)")
    abstract suspend fun deleteByIds(ids: List<Uuid>)

    @Query("DELETE FROM appointment_request")
    abstract suspend fun clear()

    @Upsert
    abstract suspend fun upsertRequests(requests: List<AppointmentRequestEntity>)

    @Query("DELETE FROM appointment_request_service_snapshot WHERE requestId IN (:requestIds)")
    abstract suspend fun deleteServicesForRequests(requestIds: List<Uuid>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertServices(services: List<AppointmentRequestServiceSnapshotEntity>)

    @Transaction
    open suspend fun upsertWithServices(
        requests: List<AppointmentRequestEntity>,
        services: List<AppointmentRequestServiceSnapshotEntity>
    ) {
        upsertRequests(requests)
        deleteServicesForRequests(requests.map { it.id })
        insertServices(services)
    }
}
