package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineScope
import me.bookk.core.Logger
import androidx.lifecycle.ViewModel as AndroidViewModel
import androidx.lifecycle.viewModelScope as frameworkScope

actual abstract class PlatformViewModel actual constructor() : AndroidViewModel() {
    private val internalLogger = Logger.create("ViewModel")
    protected actual val platformScope: CoroutineScope
        get() {
            return frameworkScope
        }

    override fun onCleared() {
        internalLogger.i("On clear called $this")
        super.onCleared()
    }
}
