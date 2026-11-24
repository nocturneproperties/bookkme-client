package me.bookk.database.client

import androidx.room3.AutoMigration
import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.database.ClientDaos
import me.bookk.database.converter.DurationConverter
import me.bookk.database.converter.InstantConverter
import me.bookk.database.converter.UuidConverter
import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.AppointmentSettingsDao
import me.bookk.database.dao.BookableEmployeeDao
import me.bookk.database.dao.BookingInfoDao
import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.BusinessDataExportDao
import me.bookk.database.dao.LegalDocumentStatusDao
import me.bookk.database.dao.NotificationSettingsDao
import me.bookk.database.dao.PersonalDataExportDao
import me.bookk.database.dao.SavedBusinessDao
import me.bookk.database.dao.ServiceDao
import me.bookk.database.dao.ServiceGroupDao
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
import me.bookk.database.entity.BookableEmployeeDayOffEntity
import me.bookk.database.entity.BookableEmployeeDayScheduleEntity
import me.bookk.database.entity.BookableEmployeeEntity
import me.bookk.database.entity.BookableEmployeeServiceEntity
import me.bookk.database.entity.BookableEmployeeWorkHourEntity
import me.bookk.database.entity.BookingInfoEntity
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
import me.bookk.database.entity.SavedBusinessEntity
import me.bookk.database.entity.ServiceEntity
import me.bookk.database.entity.ServiceGroupEntity
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
        SavedBusinessEntity::class,
        ServiceGroupEntity::class,
        ServiceEntity::class,
        BookableEmployeeEntity::class,
        BookableEmployeeServiceEntity::class,
        BookableEmployeeDayScheduleEntity::class,
        BookableEmployeeWorkHourEntity::class,
        BookableEmployeeDayOffEntity::class,
        BookingInfoEntity::class,
    ],
    version = 6,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
    ],
)
@ColumnTypeConverters(
    UuidConverter::class,
    DurationConverter::class,
    InstantConverter::class,
)
@ConstructedBy(ClientDatabaseConstructor::class)
abstract class ClientDatabase :
    RoomDatabase(),
    ClientDaos {

    abstract override fun profileDao(): UserProfileDao
    abstract override fun notificationSettingsDao(): NotificationSettingsDao
    abstract override fun legalDocumentStatusDao(): LegalDocumentStatusDao
    abstract override fun personalDataExportDao(): PersonalDataExportDao
    abstract override fun businessDao(): BusinessDao
    abstract override fun businessDataExportDao(): BusinessDataExportDao
    abstract override fun savedBusinessDao(): SavedBusinessDao
    abstract override fun serviceDao(): ServiceDao
    abstract override fun serviceGroupDao(): ServiceGroupDao
    abstract override fun appointmentDao(): AppointmentDao
    abstract override fun appointmentRequestDao(): AppointmentRequestDao
    abstract override fun appointmentSettingsDao(): AppointmentSettingsDao
    abstract override fun bookableEmployeeDao(): BookableEmployeeDao
    abstract override fun bookingInfoDao(): BookingInfoDao

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
