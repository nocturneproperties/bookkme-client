package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profile"
)
class UserProfileEntity(
    @PrimaryKey
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String
)