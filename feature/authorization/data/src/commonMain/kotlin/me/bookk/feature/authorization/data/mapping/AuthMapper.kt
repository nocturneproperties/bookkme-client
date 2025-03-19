package me.bookk.feature.authorization.data.mapping

import me.bookk.database.entity.UserProfileEntity
import me.bookk.feature.authorization.data.remote.model.AuthChallengeResponse
import me.bookk.feature.authorization.data.remote.model.DeleteAccountRemote
import me.bookk.feature.authorization.data.remote.model.PassKeySignUpStartInfo
import me.bookk.feature.authorization.data.remote.model.RegistrationChallengeResponse
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.data.remote.model.UserProfileRemote
import me.bookk.feature.authorization.data.remote.model.VerifyAccountCreationRequest
import me.bookk.feature.authorization.data.remote.model.VerifyAuthRequest
import me.bookk.feature.authorization.domain.api.CreateAccount
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.authorization.domain.datasource.authorization.ServerAuthenticationChallenge
import me.bookk.feature.authorization.domain.datasource.authorization.SignInData
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationData
import me.bookk.feature.authorization.domain.datasource.registration.ServerSignUpChallenge
import me.bookk.feature.authorization.domain.entity.TokenInfo
import me.bookk.feature.authorization.domain.entity.UserProfile

internal fun CreateAccount.UserData.toRemote(): PassKeySignUpStartInfo {
    return PassKeySignUpStartInfo(
        firstName = firstName,
        lastName = lastName,
        email = email
    )
}

internal fun RegistrationChallengeResponse.toDomain(): ServerSignUpChallenge {
    return ServerSignUpChallenge(
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

internal fun AuthChallengeResponse.toDomain(): ServerAuthenticationChallenge {
    return ServerAuthenticationChallenge(
        requestId = requestId,
        challengeJson = challengeJson
    )
}

internal fun SignInData.toRemote(): VerifyAuthRequest {
    return VerifyAuthRequest(
        requestId = requestId,
        deviceInfo = VerifyAuthRequest.DeviceInfo(
            deviceUUID = deviceInfo.deviceUUID,
            deviceName = deviceInfo.deviceName
        ),
        publicKeyCredentialJson = publicKeyCredentialJson
    )
}

internal fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email
    )
}

internal fun UserProfile.toRemote(): UserProfileRemote {
    return UserProfileRemote(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email
    )
}

internal fun UserProfile.toDb(): UserProfileEntity {
    return UserProfileEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email
    )
}

internal fun UserProfileRemote.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email
    )
}

internal fun DeleteAccountRequest.toRemote(): DeleteAccountRemote {
    return DeleteAccountRemote(
        requestId = requestId,
        publicKeyCredentialJson = publicKeyCredentialJson
    )
}