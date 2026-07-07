package me.bookk.feature.settings.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import me.bookk.core.data.DataSource
import me.bookk.database.dao.NotificationSettingsDao
import me.bookk.feature.settings.data.mapping.toChannelEntities
import me.bookk.feature.settings.data.mapping.toDomain
import me.bookk.feature.settings.data.mapping.toEntity
import me.bookk.feature.settings.data.mapping.toUpdateRequestRemote
import me.bookk.feature.settings.data.remote.api.NotificationRouting.Api.Notifications.Settings
import me.bookk.feature.settings.data.remote.model.NotificationSettingsRemote
import me.bookk.feature.settings.domain.api.entity.NotificationSettings
import me.bookk.feature.settings.domain.datasource.NotificationSettingsDataSource
import kotlin.uuid.Uuid

internal class CommonNotificationSettingsDataSource(
    private val httpClient: HttpClient,
    private val notificationSettingsDao: NotificationSettingsDao
) : DataSource(), NotificationSettingsDataSource {

    override suspend fun getNotificationSettings(): NotificationSettings =
        mapExceptions {
            httpClient.get(Settings())
                .body<NotificationSettingsRemote>()
                .toDomain()
        }

    override suspend fun updateNotificationSettings(settings: NotificationSettings): NotificationSettings =
        mapExceptions {
            httpClient.put(Settings()) {
                setBody(settings.toUpdateRequestRemote())
            }
                .body<NotificationSettingsRemote>()
                .toDomain()
        }

    override suspend fun getNotificationSettingsFromDB(userId: Uuid): NotificationSettings? =
        mapExceptions {
            notificationSettingsDao.getByUserId(userId)?.toDomain()
        }

    override suspend fun saveNotificationSettingsInDB(settings: NotificationSettings) {
        mapExceptions {
            notificationSettingsDao.upsertWithChildren(
                settings = settings.toEntity(),
                channels = settings.toChannelEntities()
            )
        }
    }
}
