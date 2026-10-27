package me.bookk.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import me.bookk.database.entity.BusinessDayOffEntity
import me.bookk.database.entity.BusinessDayScheduleEntity
import me.bookk.database.entity.BusinessEntity
import me.bookk.database.entity.BusinessWorkHourEntity

class BusinessLocal(
    @Embedded val entity: BusinessEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "businessId"
    )
    val daySchedules: List<BusinessDayScheduleEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "businessId"
    )
    val workHours: List<BusinessWorkHourEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "businessId"
    )
    val dayOffs: List<BusinessDayOffEntity>
)
