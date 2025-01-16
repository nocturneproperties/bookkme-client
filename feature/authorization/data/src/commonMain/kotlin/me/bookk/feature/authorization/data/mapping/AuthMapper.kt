package me.bookk.feature.authorization.data.mapping

import me.bookk.feature.authorization.data.remote.model.PassKeySignUpStartInfo
import me.bookk.feature.authorization.data.remote.model.RegistrationChallengeResponse
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.data.remote.model.VerifyAccountCreationRequest
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationData
import me.bookk.feature.authorization.domain.datasource.registration.ServerChallenge
import me.bookk.feature.authorization.domain.entity.TokenInfo

internal fun CreateAccount.UserData.toRemote(): PassKeySignUpStartInfo {
    return PassKeySignUpStartInfo(
        firstName = firstName,
        lastName = lastName,
        email = email
    )
}

internal fun RegistrationChallengeResponse.toDomain(): ServerChallenge {
    return ServerChallenge(
        userId = userId,
        displayName = displayName,
        jsonChallengeData = challenge
    )
}

internal fun TokenInfoResponse.toDomain(): TokenInfo {
    return TokenInfo(
        accessToken = accessToken,
        refreshToken = refreshToken
    )
}

internal fun RegistrationData.toRemote(): VerifyAccountCreationRequest {
    return VerifyAccountCreationRequest(
        deviceInfo = VerifyAccountCreationRequest.DeviceInfo(
            deviceName = deviceInfo.deviceName,
            deviceUUID = deviceInfo.deviceUUID
        ),
        userInfo = VerifyAccountCreationRequest.UserInfo(
            userId = userInfo.id,
            name = userInfo.name,
            lastName = userInfo.lastName,
            email = userInfo.email
        ),
        publicKeyCredentialJson = publicKeyCredentialJson
    )
}