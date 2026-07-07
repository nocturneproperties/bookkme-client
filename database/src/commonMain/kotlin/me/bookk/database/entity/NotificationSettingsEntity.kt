package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "notification_settings",
    indices = [Index("userId", unique = true)]
)
class NotificationSettingsEntity(
    @PrimaryKey
    val id: Uuid,
    val userId: Uuid,
    val appointmentEnabled: Boolean
)
