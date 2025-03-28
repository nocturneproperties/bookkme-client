package me.bookk.feature.settings.data.mapping

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.feature.settings.data.remote.model.AddPasskeyRequest
import me.bookk.feature.settings.data.remote.model.PasskeyRemote
import me.bookk.feature.settings.data.remote.model.RegistrationChallengeResponse
import me.bookk.feature.settings.domain.api.entity.Passkey
import me.bookk.feature.settings.domain.datasource.passkey.ClientSignUpResult
import me.bookk.feature.settings.domain.datasource.passkey.ServerSignUpChallenge

internal fun PasskeyRemote.toDomain(): Passkey {
    return Passkey(
        id = id,
        name = name,
        createdAt = createdAt.toLocalDateTime(TimeZone.currentSystemDefault()),
        isBackedUp = isBackedUp,
        lastUsedAt = lastUsedAt.toLocalDateTime(TimeZone.currentSystemDefault())
    )
}

internal fun RegistrationChallengeResponse.toDomain(): ServerSignUpChallenge {
    return ServerSignUpChallenge(
        requestId = requestId,
        displayName = displayName,
        jsonChallengeData = challenge
    )
}

internal fun ClientSignUpResult.toRemote(): AddPasskeyRequest {
    return AddPasskeyRequest(
        requestId = requestId,
        publicKeyCredentialJson = publicKeyCredentialJson
    )
}