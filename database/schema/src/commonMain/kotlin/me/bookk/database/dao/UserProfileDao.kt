package me.bookk.database.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Update
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.UserProfileEntity
import kotlin.uuid.Uuid

@Dao
abstract class UserProfileDao {

    @Query("select * from user_profile limit 1")
    abstract suspend fun queryProfile(): UserProfileEntity?

    @Query("select * from user_profile limit 1")
    abstract fun observeProfile(): Flow<UserProfileEntity?>

    @Upsert
    abstract suspend fun upsert(profile: UserProfileEntity)

    @Update
    abstract suspend fun update(profile: UserProfileEntity)

    @Delete
    abstract suspend fun delete(profile: UserProfileEntity)

    @Query("delete from user_profile where id = :profileId")
    abstract suspend fun deleteById(profileId: Uuid)

    @Query("delete from user_profile")
    abstract suspend fun clear()
}
