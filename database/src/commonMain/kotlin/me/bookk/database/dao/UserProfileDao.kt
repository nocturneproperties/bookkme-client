package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import me.bookk.database.entity.UserProfileEntity

@Dao
abstract class UserProfileDao {

    @Query("select * from user_profile limit 1")
    abstract suspend fun queryProfile(): UserProfileEntity?

    @Insert
    abstract suspend fun insert(profile: UserProfileEntity)

    @Update
    abstract suspend fun update(profile: UserProfileEntity)
}