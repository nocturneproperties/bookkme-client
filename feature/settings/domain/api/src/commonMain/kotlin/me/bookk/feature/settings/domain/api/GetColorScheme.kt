package me.bookk.feature.settings.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.settings.domain.api.entity.ColorScheme

interface GetColorScheme {
    suspend operator fun invoke(): ColorScheme
    fun asFlow(): Flow<ColorScheme>
}