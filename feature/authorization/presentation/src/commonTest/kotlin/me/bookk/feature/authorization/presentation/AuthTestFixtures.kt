package me.bookk.feature.authorization.presentation

import library.validation.api.ValidateEmail

internal class FakeValidateEmail : ValidateEmail {
    override fun invoke(email: String): ValidateEmail.Result {
        return if ("@" in email) {
            ValidateEmail.Result.Valid
        } else {
            ValidateEmail.Result.Invalid.Format
        }
    }
}
