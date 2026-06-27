package me.bookk.feature.authorization.domain.impl

import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.authorization.domain.api.LogOut

internal class LogOutImpl(
    private val logOutActors: List<LogOutAction>
) : LogOut {
    override suspend fun invoke() {
        logOutActors.forEach {
            runCatching { it.doOnLogOut() }
        }
    }
}
