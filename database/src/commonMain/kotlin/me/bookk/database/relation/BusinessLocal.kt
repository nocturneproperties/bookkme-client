package me.bookk.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.entity.BusinessDayOffEntity
import me.bookk.database.entity.BusinessDayScheduleEntity
import me.bookk.database.entity.BusinessEntity
import me.bookk.database.entity.BusinessWorkHourEntity

class BusinessLocal(
    @Embedded val entity: BusinessEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["businessId"],
    )
    val daySchedules: List<BusinessDayScheduleEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["businessId"],
    )
    val workHours: List<BusinessWorkHourEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["businessId"],
    )
    val dayOffs: List<BusinessDayOffEntity>,
)
