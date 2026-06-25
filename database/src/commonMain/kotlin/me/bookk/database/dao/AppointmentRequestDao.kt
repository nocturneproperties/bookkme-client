package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import me.bookk.database.entity.AppointmentRequestEntity
import me.bookk.database.entity.AppointmentRequestServiceSnapshotEntity
import me.bookk.database.relation.AppointmentRequestLocal
import kotlin.uuid.Uuid

@Dao
abstract class AppointmentRequestDao {

    @Transaction
    @Query("SELECT * FROM appointment_request WHERE businessId = :businessId")
    abstract suspend fun getAllForBusiness(businessId: Uuid): List<AppointmentRequestLocal>

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
