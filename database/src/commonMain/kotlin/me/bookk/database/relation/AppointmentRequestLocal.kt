package me.bookk.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.entity.AppointmentRequestEntity
import me.bookk.database.entity.AppointmentRequestServiceSnapshotEntity

class AppointmentRequestLocal(
    @Embedded val entity: AppointmentRequestEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["requestId"],
    )
    val services: List<AppointmentRequestServiceSnapshotEntity>,
)
