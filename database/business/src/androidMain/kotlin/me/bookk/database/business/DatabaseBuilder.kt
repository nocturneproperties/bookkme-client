package me.bookk.database.business

import android.content.Context
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getDatabaseBuilder(ctx: Context): RoomDatabase.Builder<BusinessDatabase> {
    val appContext = ctx.applicationContext
    val dbFile = appContext.getDatabasePath(BusinessDatabase.DATABASE_NAME)
    return Room.databaseBuilder<BusinessDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    ).setDriver(BundledSQLiteDriver())
}
