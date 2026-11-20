package me.bookk.database.client

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.database.account.AccountDatabase
import me.bookk.database.account.dao.LegalDocumentStatusDao
import me.bookk.database.account.dao.NotificationSettingsDao
import me.bookk.database.account.dao.PersonalDataExportDao
import me.bookk.database.account.dao.UserProfileDao
import me.bookk.database.account.entity.LegalDocumentStatusEntity
import me.bookk.database.account.entity.NotificationSettingsChannelEntity
import me.bookk.database.account.entity.NotificationSettingsEntity
import me.bookk.database.account.entity.PersonalDataExportEntity
import me.bookk.database.account.entity.UserProfileEntity
import me.bookk.database.converter.InstantConverter
import me.bookk.database.converter.UuidConverter

@Database(
    entities = [
        UserProfileEntity::class,
        NotificationSettingsEntity::class,
        NotificationSettingsChannelEntity::class,
        LegalDocumentStatusEntity::class,
        PersonalDataExportEntity::class,
    ],
    version = 1,
)
@ColumnTypeConverters(
    UuidConverter::class,
    InstantConverter::class,
)
@ConstructedBy(ClientDatabaseConstructor::class)
abstract class ClientDatabase :
    RoomDatabase(),
    AccountDatabase {

    abstract override fun profileDao(): UserProfileDao
    abstract override fun notificationSettingsDao(): NotificationSettingsDao
    abstract override fun legalDocumentStatusDao(): LegalDocumentStatusDao
    abstract override fun personalDataExportDao(): PersonalDataExportDao

    companion object {

        const val DATABASE_NAME = "bookk_me_client.db"

        fun create(builder: Builder<ClientDatabase>): ClientDatabase {
            return builder
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .fallbackToDestructiveMigration(true)
                .setQueryCoroutineContext(DispatcherProvider.io)
                .build()
        }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ClientDatabaseConstructor : RoomDatabaseConstructor<ClientDatabase> {
    override fun initialize(): ClientDatabase
}
