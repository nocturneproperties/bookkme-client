package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "bookable_employee_day_schedule",
    primaryKeys = ["employeeId", "dayOfWeek"],
    foreignKeys = [
        ForeignKey(
            entity = BookableEmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("employeeId")],
)
class BookableEmployeeDayScheduleEntity(
    val employeeId: Uuid,
    val dayOfWeek: String,
    val isActive: Boolean,
)

@Entity(
    tableName = "bookable_employee_working_time",
    primaryKeys = ["employeeId", "dayOfWeek", "from", "to"],
    foreignKeys = [
        ForeignKey(
            entity = BookableEmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("employeeId")],
)
class BookableEmployeeWorkHourEntity(
    val employeeId: Uuid,
    val dayOfWeek: String,
    val from: String,
    val to: String,
)

@Entity(
    tableName = "bookable_employee_day_off",
    primaryKeys = ["employeeId", "start", "end"],
    foreignKeys = [
        ForeignKey(
            entity = BookableEmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("employeeId")],
)
class BookableEmployeeDayOffEntity(
    val employeeId: Uuid,
    val start: String,
    val end: String,
)
