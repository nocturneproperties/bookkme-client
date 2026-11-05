package me.bookk.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

internal object DefaultBusinessRetention : Migration(22, 23) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE business ADD COLUMN retentionMonths INTEGER NOT NULL DEFAULT 36",
        )
        connection.execSQL(
            "UPDATE business SET retentionMonths = COALESCE(clientDataRetentionMonths, 36)",
        )
        connection.execSQL("ALTER TABLE business DROP COLUMN clientDataRetentionMonths")
        connection.execSQL(
            "ALTER TABLE business RENAME COLUMN retentionMonths TO clientDataRetentionMonths",
        )
    }
}
