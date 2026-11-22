package me.bookk.database

import androidx.room3.RoomDatabase
import androidx.room3.useReaderConnection

interface DatabaseStorage {
    suspend fun isPersistent(): Boolean
}

class RoomDatabaseStorage(
    private val database: RoomDatabase,
) : DatabaseStorage {

    override suspend fun isPersistent(): Boolean {
        return database.useReaderConnection { transactor ->
            transactor.usePrepared(MAIN_DATABASE_FILE_QUERY) { statement ->
                statement.step() && statement.getText(0).isNotEmpty()
            }
        }
    }

    private companion object {
        const val MAIN_DATABASE_FILE_QUERY =
            "SELECT file FROM pragma_database_list WHERE name = 'main'"
    }
}
