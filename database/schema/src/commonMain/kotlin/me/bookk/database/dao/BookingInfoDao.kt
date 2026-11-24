package me.bookk.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.BookingInfoEntity
import kotlin.uuid.Uuid

@Dao
interface BookingInfoDao {

    @Query("SELECT * FROM booking_info WHERE businessId = :businessId")
    fun observe(businessId: Uuid): Flow<BookingInfoEntity?>

    @Upsert
    suspend fun upsert(info: BookingInfoEntity)

    @Query("DELETE FROM booking_info WHERE businessId = :businessId")
    suspend fun delete(businessId: Uuid)

    @Query("DELETE FROM booking_info")
    suspend fun clear()
}
