package me.bookk.database.account.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "user_profile",
)
class UserProfileEntity(
    @PrimaryKey
    val id: Uuid,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null,
)
