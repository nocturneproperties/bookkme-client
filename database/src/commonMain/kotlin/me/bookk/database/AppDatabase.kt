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
import me.bookk.database.converter.UuidConverter
import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.ClientsDao
import me.bookk.database.dao.ServiceDao
import me.bookk.database.dao.ServiceGroupDao
import me.bookk.database.dao.UserProfileDao
import me.bookk.database.entity.BusinessEntity
import me.bookk.database.entity.ClientEntity
import me.bookk.database.entity.ServiceEntity
import me.bookk.database.entity.ServiceGroupEntity
import me.bookk.database.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        BusinessEntity::class,
        ClientEntity::class,
        ServiceEntity::class,
        ServiceGroupEntity::class
    ],
    version = 5,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5)
    ]
)
@TypeConverters(
    UuidConverter::class,
    DurationConverter::class
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): UserProfileDao
    abstract fun businessDao(): BusinessDao
    abstract fun clientDao(): ClientsDao
    abstract fun serviceDao(): ServiceDao
    abstract fun serviceGroupDao(): ServiceGroupDao

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