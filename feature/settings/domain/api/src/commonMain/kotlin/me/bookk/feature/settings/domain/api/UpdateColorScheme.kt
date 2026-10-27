package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.ColorScheme

interface UpdateColorScheme {
    suspend fun invoke(scheme: ColorScheme)
}