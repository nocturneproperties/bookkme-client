package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.Settings

interface GetSettings {
    suspend fun invoke(): Settings
}