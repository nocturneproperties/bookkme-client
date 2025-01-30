package me.bookk.feature.authorization.domain.api

interface ValidateName {
    sealed interface Result {

        data object Valid : Result

        sealed interface Invalid : Result {
            data object Length : Result
        }

        val Result.isValid: Boolean
            get() = this == Valid
    }

    fun invoke(name: String): Result
}