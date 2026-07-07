package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "notification_settings_channel",
    foreignKeys = [
        ForeignKey(
            entity = NotificationSettingsEntity::class,
            parentColumns = ["id"],
            childColumns = ["settingsId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("settingsId")]
)
class NotificationSettingsChannelEntity(
    @PrimaryKey
    val id: Uuid,
    val settingsId: Uuid,
    val channel: String,
    val enabled: Boolean
)
