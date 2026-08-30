package me.bookk.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import me.bookk.database.entity.EmployeeDayOffEntity
import me.bookk.database.entity.EmployeeDayScheduleEntity
import me.bookk.database.entity.EmployeeEntity
import me.bookk.database.entity.EmployeeServiceSnapshotEntity
import me.bookk.database.entity.EmployeeWorkHourEntity

class EmployeeLocal(
    @Embedded val entity: EmployeeEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "employeeId"
    )
    val daySchedules: List<EmployeeDayScheduleEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "employeeId"
    )
    val workHours: List<EmployeeWorkHourEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "employeeId"
    )
    val dayOffs: List<EmployeeDayOffEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "employeeId"
    )
    val services: List<EmployeeServiceSnapshotEntity>
)
