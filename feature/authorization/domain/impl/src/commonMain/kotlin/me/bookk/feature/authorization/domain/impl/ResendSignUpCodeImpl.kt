package me.bookk.feature.authorization.domain.impl

import me.bookk.feature.authorization.domain.api.ResendSignUpCode
import me.bookk.feature.authorization.domain.api.StartSignUp
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSessionDataSource

internal class ResendSignUpCodeImpl(
    private val startSignUp: StartSignUp,
    private val signUpSessionDataSource: SignUpSessionDataSource,
) : ResendSignUpCode {

    override suspend fun invoke(): StartSignUp.PendingSignUp {
        val session = signUpSessionDataSource.get() ?: throw ResendSignUpCode.Error.SignUpNotStarted()
        return runCatching { startSignUp(session.userData) }
            .onFailure { rejectSession(session, it) }
            .getOrThrow()
    }

    private fun rejectSession(session: SignUpSession, error: Throwable) {
        val rejection = error.toSignUpRejection() ?: return
        signUpSessionDataSource.save(session.dropped(rejection))
    }
}
