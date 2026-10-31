package me.bookk.feature.clients.presentation

import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionManager
import library.permissions.api.PermissionType
import library.validation.api.ValidateEmail
import library.validation.api.ValidateName
import me.bookk.feature.clients.domain.api.entity.Client
import kotlin.uuid.Uuid

internal fun stubDetachedClient(
    id: Uuid = Uuid.random(),
    name: String = "Anna",
    lastName: String = "Smith",
    phone: String? = "+380501234567",
    email: String? = "anna@example.com",
    businessId: Uuid = Uuid.random(),
    description: String? = null
) = Client.Detached(
    id = id,
    name = name,
    lastName = lastName,
    phone = phone,
    email = email,
    businessId = businessId,
    description = description
)

internal fun stubIntegratedClient(
    id: Uuid = Uuid.random(),
    name: String = "Olga",
    lastName: String = "Brown",
    description: String? = null
) = Client.Integrated(
    id = id,
    name = name,
    lastName = lastName,
    phone = "+380501234567",
    email = "olga@example.com",
    businessId = Uuid.random(),
    userId = Uuid.random(),
    description = description
)

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
    override val type: PermissionType,
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

internal fun permissionManagerOf(vararg checkers: FakePermissionChecker): PermissionManager {
    return PermissionManager(
        checkers = checkers.associateBy { it.type },
        fallbackChecker = FakePermissionChecker(PermissionType.NONE, granted = false)
    )
}
