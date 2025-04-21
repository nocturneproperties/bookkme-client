package me.bookk.feature.settings.domain.impl

import me.bookk.feature.settings.domain.api.DeletePasskey
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource

internal class DeletePasskeyImpl(
    private val passkeySettingsDataSource: PasskeySettingsDataSource
) : DeletePasskey {
    override suspend fun invoke(id: Long) {
        return passkeySettingsDataSource.deletePasskey(id)
    }
}