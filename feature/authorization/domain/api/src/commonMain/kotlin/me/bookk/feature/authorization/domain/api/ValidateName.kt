package me.bookk.feature.authorization.domain.api

import me.bookk.feature.authorization.domain.api.ValidateName.Result

interface ValidateName {
    sealed interface Result {

        data object Valid : Result

        sealed interface Invalid : Result {
            data object Length : Result
        }
    }

    fun invoke(name: String): Result
}

val Result.isValid: Boolean
    get() = this == Result.Valid