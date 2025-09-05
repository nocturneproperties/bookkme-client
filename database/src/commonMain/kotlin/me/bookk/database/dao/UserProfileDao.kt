package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import me.bookk.database.entity.UserProfileEntity
import kotlin.uuid.Uuid

@Dao
abstract class UserProfileDao {

    @Query("select * from user_profile limit 1")
    abstract suspend fun queryProfile(): UserProfileEntity?

    @Upsert
    abstract suspend fun upsert(profile: UserProfileEntity)

    @Update
    abstract suspend fun update(profile: UserProfileEntity)

    @Delete
    abstract suspend fun delete(profile: UserProfileEntity)

    @Query("delete from user_profile where id = :profileId")
    abstract suspend fun deleteById(profileId: Uuid)
}