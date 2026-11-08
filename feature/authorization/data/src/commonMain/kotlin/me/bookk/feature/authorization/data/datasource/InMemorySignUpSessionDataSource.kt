package me.bookk.feature.authorization.data.datasource

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSessionDataSource

internal class InMemorySignUpSessionDataSource :
    SignUpSessionDataSource,
    LogOutAction {

    private val session = MutableStateFlow<SignUpSession?>(null)

    override fun get(): SignUpSession? {
        return session.value
    }

    override fun observe(): Flow<SignUpSession?> {
        return session
    }

    override fun save(session: SignUpSession) {
        this.session.value = session
    }

    override fun clear() {
        session.value = null
    }

    override suspend fun doOnLogOut() {
        clear()
    }
}
