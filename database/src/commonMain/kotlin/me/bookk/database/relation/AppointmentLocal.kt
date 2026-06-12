package me.bookk.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import me.bookk.database.entity.AppointmentEntity
import me.bookk.database.entity.AppointmentServiceSnapshotEntity

class AppointmentLocal(
    @Embedded val entity: AppointmentEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "appointmentId"
    )
    val services: List<AppointmentServiceSnapshotEntity>
)
