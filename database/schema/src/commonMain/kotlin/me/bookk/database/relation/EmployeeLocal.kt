package me.bookk.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.entity.EmployeeDayOffEntity
import me.bookk.database.entity.EmployeeDayScheduleEntity
import me.bookk.database.entity.EmployeeEntity
import me.bookk.database.entity.EmployeeServiceSnapshotEntity
import me.bookk.database.entity.EmployeeWorkHourEntity

class EmployeeLocal(
    @Embedded val entity: EmployeeEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["employeeId"],
    )
    val daySchedules: List<EmployeeDayScheduleEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["employeeId"],
    )
    val workHours: List<EmployeeWorkHourEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["employeeId"],
    )
    val dayOffs: List<EmployeeDayOffEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["employeeId"],
    )
    val services: List<EmployeeServiceSnapshotEntity>,
)
