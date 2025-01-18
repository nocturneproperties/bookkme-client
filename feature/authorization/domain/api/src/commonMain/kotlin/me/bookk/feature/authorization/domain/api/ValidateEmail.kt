package me.bookk.feature.authorization.domain.api

interface ValidateEmail {
    sealed interface Result {

        data object Valid : Result

        sealed interface Invalid : Result {
            data object Format : Result
        }
    }

    fun invoke(email: String): Result
}

val ValidateEmail.Result.isValid: Boolean
    get() = this == ValidateEmail.Result.Valid