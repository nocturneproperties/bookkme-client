package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.entity.TokenInfo

interface GetTokenInfo {
    suspend fun invoke(): TokenInfo?
}