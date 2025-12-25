package me.bookk.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import me.bookk.core.DispatcherProvider
import me.bookk.database.converter.UuidConverter
import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.UserProfileDao
import me.bookk.database.entity.BusinessEntity
import me.bookk.database.entity.UserProfileEntity

@Database(
    entities = [
        UserProfileEntity::class,
        BusinessEntity::class
    ],
    version = 1
)
@TypeConverters(UuidConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): UserProfileDao
    abstract fun businessDao(): BusinessDao

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