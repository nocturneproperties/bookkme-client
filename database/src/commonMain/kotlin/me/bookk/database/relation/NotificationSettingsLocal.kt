package me.bookk.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import me.bookk.database.entity.NotificationSettingsChannelEntity
import me.bookk.database.entity.NotificationSettingsEntity

class NotificationSettingsLocal(
    @Embedded val entity: NotificationSettingsEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "settingsId"
    )
    val channels: List<NotificationSettingsChannelEntity>
)
