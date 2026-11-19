package library.cache.impl.preferences

import androidx.datastore.core.InterProcessCoordinator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

internal class DataChangeCoordinator(
    delegate: InterProcessCoordinator,
    private val dataKey: String,
    changedKeys: Flow<String?>,
) : InterProcessCoordinator by delegate {
    override val updateNotifications: Flow<Unit> = changedKeys
        .filter { it == null || it == dataKey }
        .map { }
}
