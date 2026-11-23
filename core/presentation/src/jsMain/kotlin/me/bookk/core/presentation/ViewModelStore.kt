package me.bookk.core.presentation

import androidx.compose.runtime.staticCompositionLocalOf

class ViewModelStore {
    private val viewModels = mutableMapOf<String, PlatformViewModel>()

    fun <T : PlatformViewModel> getOrPut(key: String, create: () -> T): T {
        @Suppress("UNCHECKED_CAST")
        return viewModels.getOrPut(key, create) as T
    }

    fun clear() {
        viewModels.values.forEach { it.onCleared() }
        viewModels.clear()
    }
}

val LocalViewModelStore = staticCompositionLocalOf<ViewModelStore?> { null }
