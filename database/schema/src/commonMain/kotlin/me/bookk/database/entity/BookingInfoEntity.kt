package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(tableName = "booking_info")
data class BookingInfoEntity(
    @PrimaryKey val businessId: Uuid,
    val appointmentNote: String,
)
