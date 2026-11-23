package me.bookk.database

import androidx.room3.RoomDatabase
import androidx.room3.Transactor
import androidx.room3.useWriterConnection
import androidx.sqlite.async.step

class ExternalChangeInvalidator(
    private val database: RoomDatabase,
) {

    private var lastDataVersion: Long? = null

    suspend fun invalidateIfChangedElsewhere(): Boolean {
        val isChanged = database.useWriterConnection { transactor ->
            val isChanged = transactor.isDataVersionChanged()
            if (isChanged) transactor.markObservedTablesInvalidated()
            isChanged
        }
        if (isChanged) database.invalidationTracker.refreshAsync()
        return isChanged
    }

    private suspend fun Transactor.isDataVersionChanged(): Boolean {
        val dataVersion = usePrepared(DATA_VERSION_QUERY) { statement ->
            statement.step()
            statement.getLong(0)
        }
        val previous = lastDataVersion
        lastDataVersion = dataVersion
        return previous != null && previous != dataVersion
    }

    private suspend fun Transactor.markObservedTablesInvalidated() {
        usePrepared(INVALIDATE_OBSERVED_TABLES_QUERY) { it.step() }
    }

    private companion object {
        const val DATA_VERSION_QUERY = "PRAGMA data_version"
        const val INVALIDATE_OBSERVED_TABLES_QUERY =
            "UPDATE room_table_modification_log SET invalidated = 1"
    }
}
