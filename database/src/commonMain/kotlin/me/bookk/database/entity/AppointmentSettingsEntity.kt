package me.bookk.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "appointment_settings",
    indices = [Index("businessId", unique = true)]
)
class AppointmentSettingsEntity(
    @PrimaryKey val id: Uuid,
    val businessId: Uuid,
    val timeZone: String,
    val automaticApproval: Boolean,
    val inBetweenBreakInMinutes: Int,
    val appointmentNote: String,
    @ColumnInfo(defaultValue = "0") val permissionView: Boolean = false,
    @ColumnInfo(defaultValue = "0") val permissionUpdate: Boolean = false,
    @ColumnInfo(defaultValue = "0") val permissionDelete: Boolean = false
)
