package me.bookk.feature.settings.domain.impl

import me.bookk.feature.settings.domain.api.DeletePasskey
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import kotlin.uuid.Uuid

internal class DeletePasskeyImpl(
    private val passkeySettingsDataSource: PasskeySettingsDataSource
) : DeletePasskey {
    override suspend fun invoke(id: Uuid) {
        return passkeySettingsDataSource.deletePasskey(id)
    }
}