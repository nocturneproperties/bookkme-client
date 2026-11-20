package me.bookk.database.account.entity

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "notification_settings",
    indices = [Index("userId", unique = true)],
)
class NotificationSettingsEntity(
    @PrimaryKey
    val id: Uuid,
    val userId: Uuid,
    val appointmentEnabled: Boolean,
)
