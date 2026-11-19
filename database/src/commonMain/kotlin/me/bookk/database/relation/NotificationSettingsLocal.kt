package me.bookk.database.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.entity.NotificationSettingsChannelEntity
import me.bookk.database.entity.NotificationSettingsEntity

class NotificationSettingsLocal(
    @Embedded val entity: NotificationSettingsEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["settingsId"],
    )
    val channels: List<NotificationSettingsChannelEntity>,
)
