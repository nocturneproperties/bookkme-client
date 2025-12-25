package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "business"
)
class BusinessEntity(
    @PrimaryKey
    val id: Uuid,
    val name: String,
    val description: String,
    val address: String,
    val locationLat: Double?,
    val locationLng: Double?,
    val currencyCode: String,
    val phone: String?,
    val insta: String?,
    val viber: String?,
    val whatsApp: String?,
    val telegram: String?
)