package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import me.bookk.database.entity.EmployeeDayOffEntity
import me.bookk.database.entity.EmployeeDayScheduleEntity
import me.bookk.database.entity.EmployeeEntity
import me.bookk.database.entity.EmployeeServiceSnapshotEntity
import me.bookk.database.entity.EmployeeWorkHourEntity
import me.bookk.database.relation.EmployeeLocal
import kotlin.uuid.Uuid

@Dao
abstract class EmployeeDao {
    @Transaction
    @Query("select * from employee where businessId = :businessId")
    abstract suspend fun getEmployees(businessId: Uuid): List<EmployeeLocal>

    @Upsert
    abstract suspend fun upsertEmployees(entities: List<EmployeeEntity>)

    @Query("delete from employee")
    abstract suspend fun clear()

    @Query("DELETE FROM employee_day_schedule WHERE employeeId = :employeeId")
    abstract suspend fun deleteDaySchedules(employeeId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDaySchedules(daySchedules: List<EmployeeDayScheduleEntity>)

    @Query("DELETE FROM employee_working_time WHERE employeeId = :employeeId")
    abstract suspend fun deleteWorkHours(employeeId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertWorkHours(workHours: List<EmployeeWorkHourEntity>)

    @Query("DELETE FROM employee_day_off WHERE employeeId = :employeeId")
    abstract suspend fun deleteDayOffs(employeeId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDayOffs(dayOffs: List<EmployeeDayOffEntity>)

    @Query("DELETE FROM employee_service_snapshot WHERE employeeId = :employeeId")
    abstract suspend fun deleteServices(employeeId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertServices(services: List<EmployeeServiceSnapshotEntity>)

    @Transaction
    open suspend fun upsertAllWithChildren(
        employees: List<EmployeeEntity>,
        daySchedules: List<EmployeeDayScheduleEntity>,
        workHours: List<EmployeeWorkHourEntity>,
        dayOffs: List<EmployeeDayOffEntity>,
        services: List<EmployeeServiceSnapshotEntity>
    ) {
        upsertEmployees(employees)
        employees.forEach { employee ->
            deleteDaySchedules(employee.id)
            deleteWorkHours(employee.id)
            deleteDayOffs(employee.id)
            deleteServices(employee.id)
        }
        insertDaySchedules(daySchedules)
        insertWorkHours(workHours)
        insertDayOffs(dayOffs)
        insertServices(services)
    }
}
