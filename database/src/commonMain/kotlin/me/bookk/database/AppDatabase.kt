package me.bookk.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import me.bookk.core.DispatcherProvider
import me.bookk.database.entity.TempEntity

@Database(
    entities = [
        TempEntity::class
    ],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
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