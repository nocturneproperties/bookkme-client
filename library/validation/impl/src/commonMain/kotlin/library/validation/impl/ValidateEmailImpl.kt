package library.validation.impl

import library.validation.api.ValidateEmail

internal class ValidateEmailImpl : ValidateEmail {
    private val regex =
        Regex(
            "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
        )

    override fun invoke(email: String): ValidateEmail.Result {
        return if (regex.matches(email.trim())) {
            ValidateEmail.Result.Valid
        } else {
            ValidateEmail.Result.Invalid.Format
        }
    }
}
