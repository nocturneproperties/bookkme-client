package me.bookk.feature.authorization.domain.api

interface IsUserLoggedIn {
    suspend fun invoke(): Boolean
}