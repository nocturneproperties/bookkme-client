package library.cache.impl.preferences

import kotlinx.browser.window
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.w3c.dom.StorageEvent
import org.w3c.dom.events.EventListener

internal fun localStorageChangedKeys(): Flow<String?> = callbackFlow {
    val listener = EventListener { event ->
        if (event is StorageEvent) {
            trySend(event.key)
        }
    }
    window.addEventListener("storage", listener)
    awaitClose { window.removeEventListener("storage", listener) }
}
