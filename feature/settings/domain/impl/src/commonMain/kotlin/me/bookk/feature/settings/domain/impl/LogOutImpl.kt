package me.bookk.feature.settings.domain.impl

import me.bookk.core.domain.logout.LogOutAction
import me.bookk.feature.settings.domain.api.LogOut

internal class LogOutImpl(
    private val logOutActors: List<LogOutAction>
) : LogOut {
    override suspend fun invoke() {
        runCatching {
            logOutActors.forEach { it.doOnLogOut() }
        }
    }
}