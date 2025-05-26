package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "business"
)
class BusinessEntity(
    @PrimaryKey
    val id: Long,
    val name: String
)