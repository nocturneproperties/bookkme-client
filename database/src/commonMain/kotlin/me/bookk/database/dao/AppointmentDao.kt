package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import me.bookk.database.entity.AppointmentEntity
import me.bookk.database.entity.AppointmentServiceSnapshotEntity
import me.bookk.database.relation.AppointmentLocal
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Dao
abstract class AppointmentDao {

    @Transaction
    @Query("SELECT * FROM appointment WHERE id = :id")
    abstract suspend fun getById(id: Uuid): AppointmentLocal

    @Transaction
    @Query("SELECT * FROM appointment WHERE businessId = :businessId AND date >= :startOfDay AND date < :endOfDay")
    abstract suspend fun getForDate(businessId: Uuid, startOfDay: Instant, endOfDay: Instant): List<AppointmentLocal>

    @Query("DELETE FROM appointment")
    abstract suspend fun clear()

    @Upsert
    abstract suspend fun upsertAppointments(appointments: List<AppointmentEntity>)

    @Query("DELETE FROM appointment_service_snapshot WHERE appointmentId IN (:appointmentIds)")
    abstract suspend fun deleteServicesForAppointments(appointmentIds: List<Uuid>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertServices(services: List<AppointmentServiceSnapshotEntity>)

    @Query("UPDATE appointment SET status = :status, cancellationReason = :cancellationReason WHERE id = :id")
    abstract suspend fun updateStatus(id: Uuid, status: String, cancellationReason: String)

    @Transaction
    open suspend fun upsertWithServices(
        appointments: List<AppointmentEntity>,
        services: List<AppointmentServiceSnapshotEntity>
    ) {
        upsertAppointments(appointments)
        deleteServicesForAppointments(appointments.map { it.id })
        insertServices(services)
    }
}
