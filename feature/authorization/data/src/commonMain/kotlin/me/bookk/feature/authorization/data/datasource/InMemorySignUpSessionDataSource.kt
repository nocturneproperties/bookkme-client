package me.bookk.feature.authorization.data.datasource

import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSession
import me.bookk.feature.authorization.domain.datasource.registration.SignUpSessionDataSource

internal class InMemorySignUpSessionDataSource :
    SignUpSessionDataSource,
    LogOutAction {

    private var session: SignUpSession? = null

    override fun get(): SignUpSession? {
        return session
    }

    override fun save(session: SignUpSession) {
        this.session = session
    }

    override fun clear() {
        session = null
    }

    override suspend fun doOnLogOut() {
        clear()
    }
}
