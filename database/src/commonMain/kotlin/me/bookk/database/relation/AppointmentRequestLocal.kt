package me.bookk.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import me.bookk.database.entity.AppointmentRequestEntity
import me.bookk.database.entity.AppointmentRequestServiceSnapshotEntity

class AppointmentRequestLocal(
    @Embedded val entity: AppointmentRequestEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "requestId"
    )
    val services: List<AppointmentRequestServiceSnapshotEntity>
)
