package me.bookk.core.domain.fetch

interface BackgroundDataFetchAction {
    suspend fun doOnBackgroundDataFetch()
}
