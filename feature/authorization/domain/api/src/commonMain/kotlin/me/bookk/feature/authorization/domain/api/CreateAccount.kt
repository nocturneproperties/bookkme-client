package me.bookk.feature.authorization.domain.api

interface CreateAccount {
    class UserData(
        val firstName: String,
        val lastName: String,
        val email: String
    )

    suspend operator fun invoke(userData: UserData)

    sealed class Error : Throwable() {
        class EmailAlreadyExist : Error()
        class InvalidEmailFormat : Error()
        class PasskeyVerificationFailed : Error()
        class AccountCreationFailed : Error()
    }
}