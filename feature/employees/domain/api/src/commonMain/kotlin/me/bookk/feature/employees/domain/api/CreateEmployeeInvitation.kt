package me.bookk.feature.employees.domain.api

import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import kotlin.uuid.Uuid

interface CreateEmployeeInvitation {
    suspend operator fun invoke(businessId: Uuid): EmployeeInvitation

    sealed interface Error {
        class PendingInvitationsLimitReached(cause: Throwable) :
            Throwable(cause),
            Error
        class DailyInvitationsLimitReached(cause: Throwable) :
            Throwable(cause),
            Error
    }
}
