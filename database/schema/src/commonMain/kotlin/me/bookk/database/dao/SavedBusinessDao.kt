package me.bookk.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.SavedBusinessEntity

@Dao
abstract class SavedBusinessDao {

    @Query("SELECT * FROM saved_business ORDER BY savedAt DESC")
    abstract fun observeAll(): Flow<List<SavedBusinessEntity>>

    @Upsert
    abstract suspend fun upsert(businesses: List<SavedBusinessEntity>)

    @Query("DELETE FROM saved_business")
    abstract suspend fun clear()

    @Transaction
    open suspend fun replaceAll(businesses: List<SavedBusinessEntity>) {
        clear()
        upsert(businesses)
    }
}
