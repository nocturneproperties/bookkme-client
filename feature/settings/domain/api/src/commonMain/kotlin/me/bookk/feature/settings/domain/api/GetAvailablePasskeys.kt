package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.Passkey

interface GetAvailablePasskeys {
    suspend operator fun invoke(): List<Passkey>
}