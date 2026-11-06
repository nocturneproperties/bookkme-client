package me.bookk.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import me.bookk.database.entity.DEFAULT_CLIENT_DATA_RETENTION_MONTHS

internal object DefaultBusinessRetention : Migration(22, 23) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(
            "ALTER TABLE business ADD COLUMN retentionMonths INTEGER NOT NULL " +
                "DEFAULT $DEFAULT_CLIENT_DATA_RETENTION_MONTHS",
        )
        connection.execSQL(
            "UPDATE business SET retentionMonths = " +
                "COALESCE(clientDataRetentionMonths, $DEFAULT_CLIENT_DATA_RETENTION_MONTHS)",
        )
        connection.execSQL("ALTER TABLE business DROP COLUMN clientDataRetentionMonths")
        connection.execSQL(
            "ALTER TABLE business RENAME COLUMN retentionMonths TO clientDataRetentionMonths",
        )
    }
}
