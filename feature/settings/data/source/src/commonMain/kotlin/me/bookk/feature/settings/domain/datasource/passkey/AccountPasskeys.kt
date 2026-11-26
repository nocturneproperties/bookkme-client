package me.bookk.feature.settings.domain.datasource.passkey

import me.bookk.feature.settings.domain.api.entity.Passkey

data class AccountPasskeys(
    val relyingParty: String,
    val userHandle: String,
    val passkeys: List<Passkey>,
    val credentialIds: List<String>,
)
