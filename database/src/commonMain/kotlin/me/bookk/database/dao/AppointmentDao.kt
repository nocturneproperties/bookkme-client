package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.AppointmentAdjustmentServiceEntity
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

    @Transaction
    @Query("SELECT * FROM appointment WHERE businessId = :businessId AND date >= :startOfDay AND date < :endOfDay AND (:employeeId IS NULL OR employeeId = :employeeId)")
    abstract fun observeForDate(
        businessId: Uuid,
        startOfDay: Instant,
        endOfDay: Instant,
        employeeId: Uuid?
    ): Flow<List<AppointmentLocal>>

    @Query("SELECT id FROM appointment WHERE businessId = :businessId AND date >= :startOfDay AND date < :endOfDay AND (:employeeId IS NULL OR employeeId = :employeeId)")
    abstract suspend fun getIdsForDate(
        businessId: Uuid,
        startOfDay: Instant,
        endOfDay: Instant,
        employeeId: Uuid?
    ): List<Uuid>

    @Query("DELETE FROM appointment WHERE id IN (:ids)")
    abstract suspend fun deleteByIds(ids: List<Uuid>)

    @Query("DELETE FROM appointment")
    abstract suspend fun clear()

    @Upsert
    abstract suspend fun upsertAppointments(appointments: List<AppointmentEntity>)

    @Query("DELETE FROM appointment_service_snapshot WHERE appointmentId IN (:appointmentIds)")
    abstract suspend fun deleteServicesForAppointments(appointmentIds: List<Uuid>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertServices(services: List<AppointmentServiceSnapshotEntity>)

    @Query("DELETE FROM appointment_adjustment_service WHERE appointmentId IN (:appointmentIds)")
    abstract suspend fun deleteAdjustmentServicesForAppointments(appointmentIds: List<Uuid>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertAdjustmentServices(services: List<AppointmentAdjustmentServiceEntity>)

    @Transaction
    open suspend fun upsertWithServices(
        appointments: List<AppointmentEntity>,
        services: List<AppointmentServiceSnapshotEntity>,
        adjustmentServices: List<AppointmentAdjustmentServiceEntity>
    ) {
        val appointmentIds = appointments.map { it.id }
        upsertAppointments(appointments)
        deleteServicesForAppointments(appointmentIds)
        deleteAdjustmentServicesForAppointments(appointmentIds)
        insertServices(services)
        insertAdjustmentServices(adjustmentServices)
    }
}
