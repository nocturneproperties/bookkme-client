package me.bookk.feature.business.domain.api.business

interface JoinBusiness {
    suspend operator fun invoke(code: String)

    sealed interface Error {
        class AlreadyProcessed(cause: Throwable) : Error, Throwable(cause)
        class EmployeeExists(cause: Throwable) : Error, Throwable(cause)
    }
}
