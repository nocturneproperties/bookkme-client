package me.bookk.database.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "notification_settings_channel",
    foreignKeys = [
        ForeignKey(
            entity = NotificationSettingsEntity::class,
            parentColumns = ["id"],
            childColumns = ["settingsId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("settingsId")],
)
class NotificationSettingsChannelEntity(
    @PrimaryKey
    val id: Uuid,
    val settingsId: Uuid,
    val channel: String,
    val enabled: Boolean,
    @ColumnInfo(defaultValue = "1") val availableToClients: Boolean,
)
