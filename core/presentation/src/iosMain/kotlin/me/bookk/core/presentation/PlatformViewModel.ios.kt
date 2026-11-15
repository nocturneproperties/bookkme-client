package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import me.bookk.core.Logger
import me.bookk.core.UsedInSwift
import me.bookk.core.coroutine.DispatcherProvider

actual abstract class PlatformViewModel actual constructor() {
    private val internalLogger = Logger.create("ViewModel")
    protected actual val platformScope = CoroutineScope(SupervisorJob() + DispatcherProvider.main)

    @UsedInSwift
    fun onCleared() {
        internalLogger.i("On clear called $this")
        platformScope.cancel()
    }
}
