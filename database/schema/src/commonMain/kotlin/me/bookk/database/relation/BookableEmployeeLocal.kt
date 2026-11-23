package me.bookk.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.entity.BookableEmployeeDayOffEntity
import me.bookk.database.entity.BookableEmployeeDayScheduleEntity
import me.bookk.database.entity.BookableEmployeeEntity
import me.bookk.database.entity.BookableEmployeeServiceEntity
import me.bookk.database.entity.BookableEmployeeWorkHourEntity

class BookableEmployeeLocal(
    @Embedded val entity: BookableEmployeeEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["employeeId"],
    )
    val services: List<BookableEmployeeServiceEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["employeeId"],
    )
    val daySchedules: List<BookableEmployeeDayScheduleEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["employeeId"],
    )
    val workHours: List<BookableEmployeeWorkHourEntity>,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["employeeId"],
    )
    val dayOffs: List<BookableEmployeeDayOffEntity>,
)
