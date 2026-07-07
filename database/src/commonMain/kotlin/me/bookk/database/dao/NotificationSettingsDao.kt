package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import me.bookk.database.entity.NotificationSettingsChannelEntity
import me.bookk.database.entity.NotificationSettingsEntity
import me.bookk.database.relation.NotificationSettingsLocal
import kotlin.uuid.Uuid

@Dao
abstract class NotificationSettingsDao {

    @Transaction
    @Query("SELECT * FROM notification_settings WHERE userId = :userId")
    abstract suspend fun getByUserId(userId: Uuid): NotificationSettingsLocal?

    @Upsert
    abstract suspend fun upsertSettings(settings: NotificationSettingsEntity)

    @Query("DELETE FROM notification_settings_channel WHERE settingsId = :settingsId")
    abstract suspend fun deleteChannels(settingsId: Uuid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertChannels(channels: List<NotificationSettingsChannelEntity>)

    @Transaction
    open suspend fun upsertWithChildren(
        settings: NotificationSettingsEntity,
        channels: List<NotificationSettingsChannelEntity>
    ) {
        upsertSettings(settings)
        deleteChannels(settings.id)
        insertChannels(channels)
    }
}
