package library.validation.api

interface ValidateEmail {
    sealed interface Result {

        data object Valid : Result

        sealed interface Invalid : Result {
            data object Format : Result
        }

        val Result.isValid: Boolean
            get() = this == Valid
    }

    operator fun invoke(email: String): Result
}