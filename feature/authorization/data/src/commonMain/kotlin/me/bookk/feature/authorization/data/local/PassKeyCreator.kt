package me.bookk.feature.authorization.data.local

import me.bookk.feature.authorization.domain.datasource.registration.PasskeyVerificationPayload
import me.bookk.feature.authorization.domain.datasource.registration.ServerChallenge

interface PassKeyCreator {
    suspend fun create(challenge: ServerChallenge): PasskeyVerificationPayload
}