package me.bookk.core.presentation

import kotlinx.coroutines.CoroutineScope

expect abstract class PlatformViewModel() {
    protected val platformScope: CoroutineScope
}
