package me.bookk.feature.business.domain.api.business

interface JoinBusiness {
    suspend operator fun invoke(code: String)

    sealed interface Error {
        class EmptyCode :
            Throwable(),
            Error
        class AlreadyProcessed(
            cause: Throwable,
        ) : Throwable(cause),
            Error
        class EmployeeExists(
            cause: Throwable,
        ) : Throwable(cause),
            Error
    }
}
