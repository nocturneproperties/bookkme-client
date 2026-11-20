package me.bookk.database.business.migration

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.async.executeSQL
import me.bookk.database.business.entity.DEFAULT_CLIENT_DATA_RETENTION_MONTHS

internal object DefaultBusinessRetention : Migration(22, 23) {
    override suspend fun migrate(connection: SQLiteConnection) {
        connection.executeSQL(
            "ALTER TABLE business ADD COLUMN retentionMonths INTEGER NOT NULL " +
                "DEFAULT $DEFAULT_CLIENT_DATA_RETENTION_MONTHS",
        )
        connection.executeSQL(
            "UPDATE business SET retentionMonths = " +
                "COALESCE(clientDataRetentionMonths, $DEFAULT_CLIENT_DATA_RETENTION_MONTHS)",
        )
        connection.executeSQL("ALTER TABLE business DROP COLUMN clientDataRetentionMonths")
        connection.executeSQL(
            "ALTER TABLE business RENAME COLUMN retentionMonths TO clientDataRetentionMonths",
        )
    }
}
