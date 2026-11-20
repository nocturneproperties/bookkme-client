package me.bookk.database.account.relation

import androidx.room3.Embedded
import androidx.room3.Relation
import me.bookk.database.account.entity.NotificationSettingsChannelEntity
import me.bookk.database.account.entity.NotificationSettingsEntity

class NotificationSettingsLocal(
    @Embedded val entity: NotificationSettingsEntity,
    @Relation(
        parentColumns = ["id"],
        entityColumns = ["settingsId"],
    )
    val channels: List<NotificationSettingsChannelEntity>,
)
