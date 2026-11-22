package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "business_link")
data class BusinessLinkEntity(
    @PrimaryKey val businessId: Uuid,
    val name: String,
)
