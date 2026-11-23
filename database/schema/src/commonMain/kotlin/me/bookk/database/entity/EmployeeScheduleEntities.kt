package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import kotlin.uuid.Uuid

@Entity(
    tableName = "employee_day_schedule",
    primaryKeys = ["employeeId", "dayOfWeek"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("employeeId")],
)
class EmployeeDayScheduleEntity(
    val employeeId: Uuid,
    val dayOfWeek: String,
    val isActive: Boolean,
)

@Entity(
    tableName = "employee_working_time",
    primaryKeys = ["employeeId", "dayOfWeek", "from", "to"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("employeeId")],
)
class EmployeeWorkHourEntity(
    val employeeId: Uuid,
    val dayOfWeek: String,
    val from: String,
    val to: String,
)

@Entity(
    tableName = "employee_day_off",
    primaryKeys = ["employeeId", "start", "end"],
    foreignKeys = [
        ForeignKey(
            entity = EmployeeEntity::class,
            parentColumns = ["id"],
            childColumns = ["employeeId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("employeeId")],
)
class EmployeeDayOffEntity(
    val employeeId: Uuid,
    val start: String,
    val end: String,
)
