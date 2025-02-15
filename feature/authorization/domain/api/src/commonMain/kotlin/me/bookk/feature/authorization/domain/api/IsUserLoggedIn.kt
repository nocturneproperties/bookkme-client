package me.bookk.feature.authorization.domain.api

interface IsUserLoggedIn {
    suspend operator fun invoke(): Boolean
}