package me.bookk.database.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.BookableEmployeeDayOffEntity
import me.bookk.database.entity.BookableEmployeeDayScheduleEntity
import me.bookk.database.entity.BookableEmployeeEntity
import me.bookk.database.entity.BookableEmployeeServiceEntity
import me.bookk.database.entity.BookableEmployeeWorkHourEntity
import me.bookk.database.relation.BookableEmployeeLocal
import kotlin.uuid.Uuid

@Dao
abstract class BookableEmployeeDao {
    @Transaction
    @Query("select * from bookable_employee where businessId = :businessId order by position")
    abstract fun observeEmployees(businessId: Uuid): Flow<List<BookableEmployeeLocal>>

    @Query("delete from bookable_employee where businessId = :businessId")
    abstract suspend fun deleteForBusiness(businessId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertEmployees(employees: List<BookableEmployeeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertServices(services: List<BookableEmployeeServiceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDaySchedules(daySchedules: List<BookableEmployeeDayScheduleEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertWorkHours(workHours: List<BookableEmployeeWorkHourEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDayOffs(dayOffs: List<BookableEmployeeDayOffEntity>)

    @Query("delete from bookable_employee")
    abstract suspend fun clear()

    @Transaction
    open suspend fun replaceForBusiness(
        businessId: Uuid,
        employees: List<BookableEmployeeEntity>,
        services: List<BookableEmployeeServiceEntity>,
        daySchedules: List<BookableEmployeeDayScheduleEntity>,
        workHours: List<BookableEmployeeWorkHourEntity>,
        dayOffs: List<BookableEmployeeDayOffEntity>,
    ) {
        deleteForBusiness(businessId)
        insertEmployees(employees)
        insertServices(services)
        insertDaySchedules(daySchedules)
        insertWorkHours(workHours)
        insertDayOffs(dayOffs)
    }
}
