package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "business_day_schedule",
    primaryKeys = ["businessId", "dayOfWeek"],
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("businessId")]
)
class BusinessDayScheduleEntity(
    val businessId: Uuid,
    val dayOfWeek: String,
    val isActive: Boolean
)

@Entity(
    tableName = "business_working_time",
    primaryKeys = ["businessId", "dayOfWeek", "from", "to"],
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("businessId")]
)
class BusinessWorkHourEntity(
    val businessId: Uuid,
    val dayOfWeek: String,
    val from: String,
    val to: String
)

@Entity(
    tableName = "business_day_off",
    primaryKeys = ["businessId", "start", "end"],
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("businessId")]
)
class BusinessDayOffEntity(
    val businessId: Uuid,
    val start: String,
    val end: String
)
