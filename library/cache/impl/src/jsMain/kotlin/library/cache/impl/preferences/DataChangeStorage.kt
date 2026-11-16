package library.cache.impl.preferences

import androidx.datastore.core.InterProcessCoordinator
import androidx.datastore.core.Storage
import androidx.datastore.core.StorageConnection
import kotlinx.coroutines.flow.Flow

internal class DataChangeStorage<T>(
    private val delegate: Storage<T>,
    private val dataKey: String,
    private val changedKeys: Flow<String?>,
) : Storage<T> {
    override fun createConnection(): StorageConnection<T> {
        val connection = delegate.createConnection()
        val coordinator = DataChangeCoordinator(connection.coordinator, dataKey, changedKeys)
        return DataChangeStorageConnection(connection, coordinator)
    }
}

private class DataChangeStorageConnection<T>(
    delegate: StorageConnection<T>,
    override val coordinator: InterProcessCoordinator,
) : StorageConnection<T> by delegate
