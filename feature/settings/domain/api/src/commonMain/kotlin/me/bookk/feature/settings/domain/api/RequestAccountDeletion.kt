package me.bookk.feature.settings.domain.api

import me.bookk.feature.settings.domain.api.entity.ActionOutcome

interface RequestAccountDeletion {
    suspend operator fun invoke(): ActionOutcome<Unit>

    sealed class Error : Throwable() {
        class AccountVerificationFailed : Error()
        class DeletionOnHold : Error()
    }
}
