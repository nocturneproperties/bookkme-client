package me.bookk.feature.settings.data.mapping

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.feature.settings.data.remote.model.AddPasskeyRequest
import me.bookk.feature.settings.data.remote.model.PasskeyListRemote
import me.bookk.feature.settings.data.remote.model.PasskeyRemote
import me.bookk.feature.settings.data.remote.model.RegistrationChallengeResponse
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.domain.datasource.passkey.AccountPasskeys
import me.bookk.feature.settings.domain.datasource.passkey.AddPasskeyChallenge
import me.bookk.feature.settings.domain.datasource.passkey.ClientSignUpResult

internal fun PasskeyRemote.toDomain(): Passkey {
    return Passkey(
        id = id,
        name = name,
        createdAt = createdAt.toLocalDateTime(TimeZone.currentSystemDefault()),
        isBackedUp = isBackedUp,
        lastUsedAt = lastUsedAt.toLocalDateTime(TimeZone.currentSystemDefault()),
    )
}

internal fun PasskeyListRemote.toDomain(): AccountPasskeys {
    return AccountPasskeys(
        relyingParty = rpId,
        userHandle = userHandle,
        passkeys = passkeys.map(PasskeyRemote::toDomain),
        credentialIds = passkeys.map(PasskeyRemote::credentialId),
    )
}

internal fun RegistrationChallengeResponse.toDomain(): AddPasskeyChallenge {
    return AddPasskeyChallenge(
        requestId = requestId,
        displayName = displayName,
        jsonChallengeData = challengeJson,
        challenge = challenge,
        userHandle = userHandle,
    )
}

internal fun ClientSignUpResult.toRemote(): AddPasskeyRequest {
    return AddPasskeyRequest(
        requestId = requestId,
        publicKeyCredentialJson = publicKeyCredentialJson,
    )
}
