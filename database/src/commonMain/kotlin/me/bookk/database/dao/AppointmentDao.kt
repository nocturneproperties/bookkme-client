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
import kotlin.uuid.Uuid

@Dao
abstract class AppointmentDao {

    @Transaction
    @Query("SELECT * FROM appointment WHERE businessId = :businessId AND localDate = :localDate")
    abstract suspend fun getForDate(businessId: Uuid, localDate: String): List<AppointmentLocal>

    @Transaction
    @Query("SELECT * FROM appointment WHERE id = :id")
    abstract suspend fun getById(id: Uuid): AppointmentLocal

    @Upsert
    abstract suspend fun upsertAppointments(appointments: List<AppointmentEntity>)

    @Query("DELETE FROM appointment_service_snapshot WHERE appointmentId IN (:appointmentIds)")
    abstract suspend fun deleteServicesForAppointments(appointmentIds: List<Uuid>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertServices(services: List<AppointmentServiceSnapshotEntity>)

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
