package me.bookk.core.domain.logout

interface LogOutAction {
    suspend fun doOnLogOut()
}