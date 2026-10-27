package me.bookk.feature.settings.domain.impl

import me.bookk.feature.settings.domain.api.GetAvailablePasskeys
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource

internal class GetAvailablePasskeysImpl(
    private val passkeySettingsDataSource: PasskeySettingsDataSource
) : GetAvailablePasskeys {
    override suspend fun invoke(): List<Passkey> {
        return passkeySettingsDataSource.getPasskeys()
    }
}