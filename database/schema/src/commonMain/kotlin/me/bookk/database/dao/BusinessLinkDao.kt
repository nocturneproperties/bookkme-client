package me.bookk.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.BusinessLinkEntity
import kotlin.uuid.Uuid

@Dao
abstract class BusinessLinkDao {

    @Query("SELECT * FROM business_link WHERE businessId = :businessId")
    abstract fun observe(businessId: Uuid): Flow<BusinessLinkEntity?>

    @Upsert
    abstract suspend fun upsert(link: BusinessLinkEntity)

    @Query("DELETE FROM business_link WHERE businessId = :businessId")
    abstract suspend fun delete(businessId: Uuid)

    @Query("DELETE FROM business_link")
    abstract suspend fun clear()
}
