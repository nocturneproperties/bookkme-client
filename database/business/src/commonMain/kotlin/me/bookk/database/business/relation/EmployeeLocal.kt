package me.bookk.database.business.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.business.entity.EmployeeDayOffEntity
import me.bookk.database.business.entity.EmployeeDayScheduleEntity
import me.bookk.database.business.entity.EmployeeEntity
import me.bookk.database.business.entity.EmployeeServiceSnapshotEntity
import me.bookk.database.business.entity.EmployeeWorkHourEntity

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
