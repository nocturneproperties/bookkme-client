package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.PersonalDataExportEntity

@Dao
abstract class PersonalDataExportDao {

    @Query("SELECT * FROM personal_data_export WHERE id = 0")
    abstract fun observe(): Flow<PersonalDataExportEntity?>

    @Upsert
    abstract suspend fun upsert(export: PersonalDataExportEntity)

    @Query("DELETE FROM personal_data_export")
    abstract suspend fun clear()
}
