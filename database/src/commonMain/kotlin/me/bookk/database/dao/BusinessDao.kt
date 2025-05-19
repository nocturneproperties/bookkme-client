package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.BusinessEntity

@Dao
abstract class BusinessDao {
    @Query("select * from business limit 1")
    abstract suspend fun queryBusiness(): BusinessEntity?

    @Query("select * from business limit 1")
    abstract fun observeBusiness(): Flow<BusinessEntity?>

    @Insert
    abstract suspend fun insertBusiness(entity: BusinessEntity)

    @Upsert
    abstract suspend fun upsertBusiness(entity: BusinessEntity)

    @Query("delete from business")
    abstract suspend fun clear()
}