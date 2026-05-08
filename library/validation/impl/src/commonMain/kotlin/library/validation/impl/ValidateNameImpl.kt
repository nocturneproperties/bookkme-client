package library.validation.impl

import library.validation.api.ValidateName
import library.validation.api.ValidateName.Result

internal class ValidateNameImpl : ValidateName {
    override fun invoke(name: String): Result {
        return when (name.length) {
            in MIN_LENGTH..Int.MAX_VALUE -> Result.Valid
            else -> Result.Invalid.Length
        }
    }

    companion object {
        private const val MIN_LENGTH = 2
    }
}