package me.bookk.database.client

import androidx.room3.AutoMigration
import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.database.converter.DurationConverter
import me.bookk.database.converter.InstantConverter
import me.bookk.database.converter.UuidConverter
import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.AppointmentSettingsDao
import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.BusinessDataExportDao
import me.bookk.database.dao.LegalDocumentStatusDao
import me.bookk.database.dao.NotificationSettingsDao
import me.bookk.database.dao.PersonalDataExportDao
import me.bookk.database.dao.UserProfileDao
import me.bookk.database.entity.AppointmentAdjustmentServiceEntity
import me.bookk.database.entity.AppointmentEntity
import me.bookk.database.entity.AppointmentRequestEntity
import me.bookk.database.entity.AppointmentRequestServiceSnapshotEntity
import me.bookk.database.entity.AppointmentServiceSnapshotEntity
import me.bookk.database.entity.AppointmentSettingsDayOffEntity
import me.bookk.database.entity.AppointmentSettingsDayScheduleEntity
import me.bookk.database.entity.AppointmentSettingsEntity
import me.bookk.database.entity.AppointmentSettingsWorkHourEntity
import me.bookk.database.entity.BusinessDataExportEntity
import me.bookk.database.entity.BusinessDayOffEntity
import me.bookk.database.entity.BusinessDayScheduleEntity
import me.bookk.database.entity.BusinessEntity
import me.bookk.database.entity.BusinessWorkHourEntity
import me.bookk.database.entity.DashboardBusinessEntity
import me.bookk.database.entity.LegalDocumentStatusEntity
import me.bookk.database.entity.NotificationSettingsChannelEntity
import me.bookk.database.entity.NotificationSettingsEntity
import me.bookk.database.entity.PersonalDataExportEntity
import me.bookk.database.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        NotificationSettingsEntity::class,
        NotificationSettingsChannelEntity::class,
        LegalDocumentStatusEntity::class,
        PersonalDataExportEntity::class,
        BusinessEntity::class,
        BusinessDayScheduleEntity::class,
        BusinessWorkHourEntity::class,
        BusinessDayOffEntity::class,
        DashboardBusinessEntity::class,
        BusinessDataExportEntity::class,
        AppointmentEntity::class,
        AppointmentServiceSnapshotEntity::class,
        AppointmentAdjustmentServiceEntity::class,
        AppointmentRequestEntity::class,
        AppointmentRequestServiceSnapshotEntity::class,
        AppointmentSettingsEntity::class,
        AppointmentSettingsDayScheduleEntity::class,
        AppointmentSettingsWorkHourEntity::class,
        AppointmentSettingsDayOffEntity::class,
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
    ],
)
@ColumnTypeConverters(
    UuidConverter::class,
    DurationConverter::class,
    InstantConverter::class,
)
@ConstructedBy(ClientDatabaseConstructor::class)
abstract class ClientDatabase : RoomDatabase() {

    abstract fun profileDao(): UserProfileDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao
    abstract fun legalDocumentStatusDao(): LegalDocumentStatusDao
    abstract fun personalDataExportDao(): PersonalDataExportDao
    abstract fun businessDao(): BusinessDao
    abstract fun businessDataExportDao(): BusinessDataExportDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun appointmentRequestDao(): AppointmentRequestDao
    abstract fun appointmentSettingsDao(): AppointmentSettingsDao

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
