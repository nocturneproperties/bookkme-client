package me.bookk.core.domain.fetch

interface AppStartAction {
    suspend fun doOnAppStart()
}
