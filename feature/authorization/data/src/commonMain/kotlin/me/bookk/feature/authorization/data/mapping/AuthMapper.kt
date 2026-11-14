package me.bookk.feature.authorization.data.mapping

import me.bookk.database.entity.UserProfileEntity
import me.bookk.feature.authorization.data.remote.model.AcceptedLegalDocumentsRemote
import me.bookk.feature.authorization.data.remote.model.AuthChallengeResponse
import me.bookk.feature.authorization.data.remote.model.DeleteAccountRequestRemote
import me.bookk.feature.authorization.data.remote.model.EmailVerificationStartedRemote
import me.bookk.feature.authorization.data.remote.model.PassKeySignUpStartInfo
import me.bookk.feature.authorization.data.remote.model.SignUpChallengeResponseRemote
import me.bookk.feature.authorization.data.remote.model.TokenInfoResponse
import me.bookk.feature.authorization.data.remote.model.UserProfileRemote
import me.bookk.feature.authorization.data.remote.model.VerifyAccountCreationRequest
import me.bookk.feature.authorization.data.remote.model.VerifyAuthRequest
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.datasource.authorization.DeleteAccountRequest
import me.bookk.feature.authorization.domain.datasource.authorization.ServerAuthenticationChallenge
import me.bookk.feature.authorization.domain.datasource.authorization.SignInData
import me.bookk.feature.authorization.domain.datasource.registration.RegistrationData
import me.bookk.feature.authorization.domain.datasource.registration.ServerSignUpChallenge
import me.bookk.feature.authorization.domain.entity.EmailVerification
import me.bookk.feature.authorization.domain.entity.TokenInfo
import me.bookk.feature.authorization.domain.entity.UserProfile

internal fun StartSignUp.UserData.toRemote(): PassKeySignUpStartInfo {
    return PassKeySignUpStartInfo(
        firstName = firstName,
        lastName = lastName,
        email = email,
    )
}

internal fun EmailVerificationStartedRemote.toDomain(): EmailVerification {
    return EmailVerification(
        id = verificationId,
        expiresAt = expiresAt,
        resendAvailableAt = resendAvailableAt,
    )
}

internal fun SignUpChallengeResponseRemote.toDomain(): ServerSignUpChallenge {
    return ServerSignUpChallenge(
        requestId = requestId,
        displayName = displayName,
        jsonChallengeData = challengeJson,
        challenge = challenge,
        userHandle = userHandle,
        emailVerification = emailVerification.toDomain(),
    )
}

internal fun TokenInfoResponse.toDomain(): TokenInfo {
    return TokenInfo(
        accessToken = accessToken,
        refreshToken = refreshToken,
    )
}

internal fun RegistrationData.toRemote(): VerifyAccountCreationRequest {
    return VerifyAccountCreationRequest(
        requestId = requestId,
        deviceInfo = VerifyAccountCreationRequest.DeviceInfo(
            deviceName = deviceInfo.deviceName,
            deviceUUID = deviceInfo.deviceUUID,
        ),
        userInfo = VerifyAccountCreationRequest.UserInfo(
            name = userInfo.name,
            lastName = userInfo.lastName,
            email = userInfo.email,
        ),
        publicKeyCredentialJson = publicKeyCredentialJson,
        acceptedDocuments = AcceptedLegalDocumentsRemote(
            privacyPolicy = acceptedDocuments.privacyPolicy,
            termsOfService = acceptedDocuments.termsOfService,
        ),
        verificationId = verificationId,
        emailCode = emailCode,
    )
}

internal fun AuthChallengeResponse.toDomain(): ServerAuthenticationChallenge {
    return ServerAuthenticationChallenge(
        requestId = requestId,
        challengeJson = challengeJson,
        challenge = challenge,
    )
}

internal fun SignInData.toRemote(): VerifyAuthRequest {
    return VerifyAuthRequest(
        requestId = requestId,
        deviceInfo = VerifyAuthRequest.DeviceInfo(
            deviceUUID = deviceInfo.deviceUUID,
            deviceName = deviceInfo.deviceName,
        ),
        publicKeyCredentialJson = publicKeyCredentialJson,
    )
}

internal fun UserProfileEntity.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
    )
}

internal fun UserProfile.toRemote(): UserProfileRemote {
    return UserProfileRemote(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
    )
}

internal fun UserProfile.toDb(): UserProfileEntity {
    return UserProfileEntity(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
    )
}

internal fun UserProfileRemote.toDomain(): UserProfile {
    return UserProfile(
        id = id,
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
    )
}

internal fun DeleteAccountRequest.toRemote(): DeleteAccountRequestRemote {
    return DeleteAccountRequestRemote(
        confirmationToken = confirmationToken,
    )
}
