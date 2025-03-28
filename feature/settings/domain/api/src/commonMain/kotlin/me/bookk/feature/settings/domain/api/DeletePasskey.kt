package me.bookk.feature.settings.domain.api

interface DeletePasskey {
    suspend operator fun invoke(id: Long)
}