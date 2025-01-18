package me.bookk.feature.authorization.domain.api

interface CreateAccount {
    class UserData(
        val firstName: String,
        val lastName: String,
        val email: String
    )

    suspend operator fun invoke(userData: UserData)

    sealed class Error : Throwable() {
        data object EmailAlreadyExist : Error()
        data object InvalidEmailFormat : Error()
        data object PasskeyVerificationFailed : Error()
        data object AccountCreationFailed : Error()
    }
}