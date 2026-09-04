package me.bookk.feature.authorization.domain.api

interface InitialAppDataFetch {
    suspend operator fun invoke(ignoreLastFetchTimestamp: Boolean = false)
}