package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "user_profile"
)
class UserProfileEntity(
    @PrimaryKey
    val id: Uuid,
    val firstName: String,
    val lastName: String,
    val email: String
)