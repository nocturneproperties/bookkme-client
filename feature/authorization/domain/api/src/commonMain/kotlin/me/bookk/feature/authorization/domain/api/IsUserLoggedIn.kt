package me.bookk.feature.authorization.domain.api

import kotlinx.coroutines.flow.Flow

interface IsUserLoggedIn {
    suspend operator fun invoke(): Boolean
    fun asFlow(): Flow<Boolean>
}