package library.telegram

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

suspend fun TelegramWebApp.askWriteAccess(): Boolean {
    if (!supports(TelegramFeature.WRITE_ACCESS)) return false
    return suspendCancellableCoroutine { continuation ->
        requestWriteAccess { continuation.resume(it) }
    }
}
