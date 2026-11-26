package me.bookk.feature.settings.domain.impl

import me.bookk.core.coroutine.runSuspendCatching
import me.bookk.core.domain.fetch.BackgroundDataFetchAction
import me.bookk.feature.authorization.domain.datasource.registration.PassKeyManager
import me.bookk.feature.settings.domain.api.GetAvailablePasskeys
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.domain.datasource.passkey.AccountPasskeys
import me.bookk.feature.settings.domain.datasource.passkey.PasskeySettingsDataSource

internal class GetAvailablePasskeysImpl(
    private val passkeySettingsDataSource: PasskeySettingsDataSource,
    private val passKeyManager: PassKeyManager,
) : GetAvailablePasskeys,
    BackgroundDataFetchAction {
    override suspend fun invoke(): List<Passkey> {
        val account = passkeySettingsDataSource.getPasskeys()
        signalAcceptedPasskeys(account)
        return account.passkeys
    }

    override suspend fun doOnBackgroundDataFetch() {
        invoke()
    }

    private suspend fun signalAcceptedPasskeys(account: AccountPasskeys) {
        if (account.credentialIds.any { it.isBlank() }) return
        runSuspendCatching {
            passKeyManager.signalAcceptedPasskeys(
                PassKeyManager.AcceptedPasskeys(
                    relyingParty = account.relyingParty,
                    userHandle = account.userHandle,
                    credentialIds = account.credentialIds,
                ),
            )
        }
    }
}
