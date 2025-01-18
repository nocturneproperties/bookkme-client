package me.bookk.feature.authorization.data.local

import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.ServerChallenge

expect class PassKeyCreator {
    suspend fun create(challenge: ServerChallenge): PasskeyVerificationPayload
}