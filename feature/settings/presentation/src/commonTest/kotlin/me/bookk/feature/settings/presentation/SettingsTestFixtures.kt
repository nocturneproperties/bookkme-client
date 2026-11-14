package me.bookk.feature.settings.presentation

import library.permissions.api.PermissionChecker
import library.permissions.api.PermissionManager
import library.permissions.api.PermissionType
import library.validation.api.ValidateName
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion
import kotlin.time.Instant

internal class FakeValidateName : ValidateName {
    override fun invoke(name: String): ValidateName.Result {
        return if (name.length >=
            2
        ) {
            ValidateName.Result.Valid
        } else {
            ValidateName.Result.Invalid.Length
        }
    }
}

internal class FakePermissionChecker(
    override val type: PermissionType = PermissionType.NOTIFICATIONS,
    var granted: Boolean = true,
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

internal fun privacyPolicyStatus(
    accepted: Boolean = false,
    version: String = "privacy-v2",
): LegalDocumentStatus {
    return legalDocumentStatus(LegalDocument.PRIVACY_POLICY, version, "privacy", accepted)
}

internal fun termsStatus(accepted: Boolean = true): LegalDocumentStatus {
    return legalDocumentStatus(LegalDocument.TERMS_OF_SERVICE, "terms-v3", "terms", accepted)
}

private fun legalDocumentStatus(
    document: LegalDocument,
    version: String,
    path: String,
    accepted: Boolean,
): LegalDocumentStatus {
    return LegalDocumentStatus(
        LegalDocumentVersion(
            document,
            version,
            "https://bookk.test/$path",
            Instant.parse("2026-11-01T00:00:00Z"),
        ),
        if (accepted) Instant.parse("2026-11-02T00:00:00Z") else null,
    )
}
