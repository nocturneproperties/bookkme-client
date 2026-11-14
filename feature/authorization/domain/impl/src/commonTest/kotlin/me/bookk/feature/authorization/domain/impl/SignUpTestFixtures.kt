package me.bookk.feature.authorization.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.datasource.registration.ServerSignUpChallenge
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSessionDataSource
import me.bookk.feature.authorization.domain.entity.EmailVerification
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal class InMemorySignUpSessionDataSource : SignUpSessionDataSource {
    private val state = MutableStateFlow<SignUpSession?>(null)

    var session: SignUpSession?
        get() = state.value
        set(value) {
            state.value = value
        }

    override fun get(): SignUpSession? {
        return session
    }

    override fun observe(): Flow<SignUpSession?> {
        return state
    }

    override fun save(session: SignUpSession) {
        this.session = session
    }

    override fun clear() {
        session = null
    }
}

internal val stubVerificationId = Uuid.parse("00000000-0000-0000-0000-0000000000a1")

internal fun stubEmailVerification(): EmailVerification {
    return EmailVerification(
        id = stubVerificationId,
        expiresAt = Instant.fromEpochSeconds(1_000),
        resendAvailableAt = Instant.fromEpochSeconds(100),
    )
}

internal fun stubSignUpChallenge(): ServerSignUpChallenge {
    return ServerSignUpChallenge(
        requestId = "req-id",
        displayName = "John Doe",
        jsonChallengeData = "{}",
        challenge = "chal",
        userHandle = "handle",
        emailVerification = stubEmailVerification(),
    )
}

internal fun stubSignUpUserData(): StartSignUp.UserData {
    return StartSignUp.UserData("John", "Doe", "john@example.com")
}
