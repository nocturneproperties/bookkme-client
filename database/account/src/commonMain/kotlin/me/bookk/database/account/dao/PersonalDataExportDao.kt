package me.bookk.database.account.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.account.entity.PersonalDataExportEntity

@Dao
abstract class PersonalDataExportDao {

    @Query("SELECT * FROM personal_data_export WHERE id = 0")
    abstract fun observe(): Flow<PersonalDataExportEntity?>

    @Upsert
    abstract suspend fun upsert(export: PersonalDataExportEntity)

    @Query("DELETE FROM personal_data_export")
    abstract suspend fun clear()
}
