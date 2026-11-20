package me.bookk.database.business.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.business.entity.AppointmentRequestEntity
import me.bookk.database.business.entity.AppointmentRequestServiceSnapshotEntity

class AppointmentRequestLocal(
    @Embedded val entity: AppointmentRequestEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["requestId"],
    )
    val services: List<AppointmentRequestServiceSnapshotEntity>,
)
