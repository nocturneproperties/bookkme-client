package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.ActionOutcome

interface DeleteAccount {
    suspend operator fun invoke(): ActionOutcome<Unit>

    sealed class Error : Throwable() {
        class AccountVerificationFailed : Error()
    }
}
