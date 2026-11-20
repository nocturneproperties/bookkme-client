package me.bookk.database.business.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.business.entity.AppointmentAdjustmentServiceEntity
import me.bookk.database.business.entity.AppointmentEntity
import me.bookk.database.business.entity.AppointmentServiceSnapshotEntity

class AppointmentLocal(
    @Embedded val entity: AppointmentEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["appointmentId"],
    )
    val services: List<AppointmentServiceSnapshotEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["appointmentId"],
    )
    val adjustmentServices: List<AppointmentAdjustmentServiceEntity>,
)
