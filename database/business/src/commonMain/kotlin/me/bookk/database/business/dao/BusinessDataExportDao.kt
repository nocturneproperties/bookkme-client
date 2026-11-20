package me.bookk.database.business.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.business.entity.BusinessDataExportEntity
import kotlin.uuid.Uuid

@Dao
abstract class BusinessDataExportDao {

    @Query("SELECT * FROM business_data_export WHERE businessId = :businessId")
    abstract fun observe(businessId: Uuid): Flow<BusinessDataExportEntity?>

    @Query("SELECT * FROM business_data_export")
    abstract suspend fun getAll(): List<BusinessDataExportEntity>

    @Upsert
    abstract suspend fun upsert(export: BusinessDataExportEntity)

    @Query("DELETE FROM business_data_export WHERE businessId = :businessId")
    abstract suspend fun delete(businessId: Uuid)

    @Query("DELETE FROM business_data_export")
    abstract suspend fun clear()
}
