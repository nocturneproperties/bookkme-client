package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.entity.onBusinessError
import me.bookk.feature.settings.domain.api.DeletePasskey
import me.bookk.feature.settings.domain.datasource.SettingsErrorCodes
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource
import kotlin.uuid.Uuid

internal class DeletePasskeyImpl(
    private val passkeySettingsDataSource: PasskeySettingsDataSource,
) : DeletePasskey {
    override suspend fun invoke(id: Uuid) {
        runCatching { passkeySettingsDataSource.deletePasskey(id) }
            .onBusinessError {
                when (it.errorCode) {
                    SettingsErrorCodes.LAST_PASSKEY -> throw DeletePasskey.Error.LastPasskey()
                }
            }.getOrThrow()
    }
}
