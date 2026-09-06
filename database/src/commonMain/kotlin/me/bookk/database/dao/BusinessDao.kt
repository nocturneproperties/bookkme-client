package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.BusinessDayOffEntity
import me.bookk.database.entity.BusinessDayScheduleEntity
import me.bookk.database.entity.BusinessEntity
import me.bookk.database.entity.BusinessWorkHourEntity
import me.bookk.database.relation.BusinessLocal
import kotlin.uuid.Uuid

@Dao
abstract class BusinessDao {
    @Transaction
    @Query("select * from business where id = :id")
    abstract suspend fun queryBusiness(id: Uuid): BusinessLocal?

    @Transaction
    @Query("select * from business where id = :businessId")
    abstract fun observeBusiness(businessId: Uuid): Flow<BusinessLocal?>

    @Transaction
    @Query("select * from business")
    abstract fun observeAllBusinesses(): Flow<List<BusinessLocal>>

    @Insert
    abstract suspend fun insertBusiness(entity: BusinessEntity)

    @Upsert
    abstract suspend fun upsertBusiness(entity: BusinessEntity)

    @Upsert
    abstract suspend fun upsertBusiness(entity: List<BusinessEntity>)

    @Query("delete from business")
    abstract suspend fun clear()

    @Query("DELETE FROM business_day_schedule WHERE businessId = :businessId")
    abstract suspend fun deleteDaySchedules(businessId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDaySchedules(daySchedules: List<BusinessDayScheduleEntity>)

    @Query("DELETE FROM business_working_time WHERE businessId = :businessId")
    abstract suspend fun deleteWorkHours(businessId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertWorkHours(workHours: List<BusinessWorkHourEntity>)

    @Query("DELETE FROM business_day_off WHERE businessId = :businessId")
    abstract suspend fun deleteDayOffs(businessId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDayOffs(dayOffs: List<BusinessDayOffEntity>)

    @Transaction
    open suspend fun upsertWithChildren(
        business: BusinessEntity,
        daySchedules: List<BusinessDayScheduleEntity>,
        workHours: List<BusinessWorkHourEntity>,
        dayOffs: List<BusinessDayOffEntity>
    ) {
        upsertBusiness(business)
        deleteDaySchedules(business.id)
        insertDaySchedules(daySchedules)
        deleteWorkHours(business.id)
        insertWorkHours(workHours)
        deleteDayOffs(business.id)
        insertDayOffs(dayOffs)
    }

    @Transaction
    open suspend fun upsertAllWithChildren(
        businesses: List<BusinessEntity>,
        daySchedules: List<BusinessDayScheduleEntity>,
        workHours: List<BusinessWorkHourEntity>,
        dayOffs: List<BusinessDayOffEntity>
    ) {
        upsertBusiness(businesses)
        businesses.forEach { business ->
            deleteDaySchedules(business.id)
            deleteWorkHours(business.id)
            deleteDayOffs(business.id)
        }
        insertDaySchedules(daySchedules)
        insertWorkHours(workHours)
        insertDayOffs(dayOffs)
    }
}
