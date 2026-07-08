package me.bookk.feature.settings.domain.api

interface UpdateNotificationToken {
    suspend operator fun invoke(token: String)
    suspend operator fun invoke()
}
