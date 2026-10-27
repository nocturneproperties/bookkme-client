package me.bookk.feature.settings.presentation

import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionManager
import library.permissions.api.PermissionType
import library.validation.api.ValidateEmail
import library.validation.api.ValidateName

internal class FakeValidateName : ValidateName {
    override fun invoke(name: String): ValidateName.Result {
        return if (name.length >= 2) ValidateName.Result.Valid else ValidateName.Result.Invalid.Length
    }
}

internal class FakeValidateEmail : ValidateEmail {
    override fun invoke(email: String): ValidateEmail.Result {
        return if ("@" in email) ValidateEmail.Result.Valid else ValidateEmail.Result.Invalid.Format
    }
}

internal class FakePermissionChecker(
    override val type: PermissionType = PermissionType.NOTIFICATIONS,
    var granted: Boolean = true
) : PermissionChecker {
    var requestCount = 0

    override suspend fun requestPermission(): Boolean {
        requestCount++
        return granted
    }

    override suspend fun hasPermission(): Boolean {
        return granted
    }
}

internal fun FakePermissionChecker.asManager(): PermissionManager {
    return PermissionManager(checkers = mapOf(type to this), fallbackChecker = this)
}
