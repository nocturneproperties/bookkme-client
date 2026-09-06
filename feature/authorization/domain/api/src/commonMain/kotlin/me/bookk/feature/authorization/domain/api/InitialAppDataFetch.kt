package me.bookk.feature.authorization.domain.api

interface InitialAppDataFetch {
    suspend fun timestampProtectedFetch()
    suspend fun rawFetch()
}