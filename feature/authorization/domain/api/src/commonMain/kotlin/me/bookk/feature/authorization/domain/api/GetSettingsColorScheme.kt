package me.bookk.feature.authorization.domain.api

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.authorization.domain.entity.ColorScheme

interface GetSettingsColorScheme {
    suspend operator fun invoke(): ColorScheme
    fun asFlow(): Flow<ColorScheme>
}
