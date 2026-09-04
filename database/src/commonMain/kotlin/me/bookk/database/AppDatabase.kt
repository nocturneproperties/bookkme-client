package me.bookk.database

import androidx.room.AutoMigration
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.database.converter.DurationConverter
import me.bookk.database.converter.InstantConverter
import me.bookk.database.converter.UuidConverter
import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.AppointmentSettingsDao
import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.ClientsDao
import me.bookk.database.dao.EmployeeDao
import me.bookk.database.dao.EmployeeInvitationDao
import me.bookk.database.dao.NotificationSettingsDao
import me.bookk.database.dao.ServiceDao
import me.bookk.database.dao.ServiceGroupDao
import me.bookk.database.dao.UserProfileDao
import me.bookk.database.entity.AppointmentEntity
import me.bookk.database.entity.AppointmentRequestEntity
import me.bookk.database.entity.AppointmentRequestServiceSnapshotEntity
import me.bookk.database.entity.AppointmentServiceSnapshotEntity
import me.bookk.database.entity.AppointmentSettingsDayOffEntity
import me.bookk.database.entity.AppointmentSettingsDayScheduleEntity
import me.bookk.database.entity.AppointmentSettingsEntity
import me.bookk.database.entity.AppointmentSettingsWorkHourEntity
import me.bookk.database.entity.BusinessDayOffEntity
import me.bookk.database.entity.BusinessDayScheduleEntity
import me.bookk.database.entity.BusinessEntity
import me.bookk.database.entity.BusinessWorkHourEntity
import me.bookk.database.entity.ClientEntity
import me.bookk.database.entity.EmployeeDayOffEntity
import me.bookk.database.entity.EmployeeDayScheduleEntity
import me.bookk.database.entity.EmployeeEntity
import me.bookk.database.entity.EmployeeInvitationEntity
import me.bookk.database.entity.EmployeeServiceSnapshotEntity
import me.bookk.database.entity.EmployeeWorkHourEntity
import me.bookk.database.entity.NotificationSettingsChannelEntity
import me.bookk.database.entity.NotificationSettingsEntity
import me.bookk.database.entity.ServiceEntity
import me.bookk.database.entity.ServiceGroupEntity
import me.bookk.database.entity.UserProfileEntity
import me.bookk.database.migration.DeleteEmployeeInvitationEmail

@Database(
    entities = [
        UserProfileEntity::class,
        BusinessEntity::class,
        BusinessDayScheduleEntity::class,
        BusinessWorkHourEntity::class,
        BusinessDayOffEntity::class,
        ClientEntity::class,
        ServiceEntity::class,
        ServiceGroupEntity::class,
        AppointmentEntity::class,
        AppointmentServiceSnapshotEntity::class,
        AppointmentSettingsEntity::class,
        AppointmentSettingsDayScheduleEntity::class,
        AppointmentSettingsWorkHourEntity::class,
        AppointmentSettingsDayOffEntity::class,
        AppointmentRequestEntity::class,
        AppointmentRequestServiceSnapshotEntity::class,
        NotificationSettingsEntity::class,
        NotificationSettingsChannelEntity::class,
        EmployeeInvitationEntity::class,
        EmployeeEntity::class,
        EmployeeDayScheduleEntity::class,
        EmployeeWorkHourEntity::class,
        EmployeeDayOffEntity::class,
        EmployeeServiceSnapshotEntity::class
    ],
    version = 14,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 11, to = 12, spec = DeleteEmployeeInvitationEmail::class),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14)
    ]
)
@TypeConverters(
    UuidConverter::class,
    DurationConverter::class,
    InstantConverter::class
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): UserProfileDao
    abstract fun businessDao(): BusinessDao
    abstract fun clientDao(): ClientsDao
    abstract fun serviceDao(): ServiceDao
    abstract fun serviceGroupDao(): ServiceGroupDao
    abstract fun appointmentDao(): AppointmentDao
    abstract fun appointmentRequestDao(): AppointmentRequestDao
    abstract fun appointmentSettingsDao(): AppointmentSettingsDao
    abstract fun notificationSettingsDao(): NotificationSettingsDao
    abstract fun employeeInvitationDao(): EmployeeInvitationDao
    abstract fun employeeDao(): EmployeeDao

    companion object {

        const val DATABASE_NAME = "bookk_me.db"

        fun create(builder: Builder<AppDatabase>): AppDatabase {
            return builder
                .fallbackToDestructiveMigrationOnDowngrade(true)
                .fallbackToDestructiveMigration(true)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(DispatcherProvider.io)
                .build()
        }
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}