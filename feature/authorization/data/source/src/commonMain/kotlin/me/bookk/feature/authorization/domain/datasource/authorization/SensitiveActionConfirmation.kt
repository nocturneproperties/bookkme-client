package me.bookk.feature.authorization.domain.datasource.authorization

import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.uuid.Uuid

data class SensitiveActionConfirmation(
    val requestId: String,
    val publicKeyCredentialJson: String,
    val action: SensitiveAction,
    val businessId: Uuid,
    val targetId: Uuid,
)
