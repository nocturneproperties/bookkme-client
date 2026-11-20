package me.bookk.core.domain.fetch

interface DataFetchAction {
    suspend fun doOnDataFetch(isSignIn: Boolean)
}
