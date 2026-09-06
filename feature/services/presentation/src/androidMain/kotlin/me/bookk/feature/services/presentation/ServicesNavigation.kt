package me.bookk.feature.services.presentation

import androidx.compose.runtime.compositionLocalOf
import kotlin.uuid.Uuid

class ServicesNavigation(
    val onBack: () -> Unit,
    val toCreateService: (Uuid) -> Unit,
    val toServiceDetails: (Uuid) -> Unit,
    val toCreateServiceGroup: (Uuid) -> Unit,
    val toServiceGroupList: () -> Unit
)

internal val LocalNavigation = compositionLocalOf {
    ServicesNavigation(
        onBack = {},
        toCreateService = {},
        toServiceDetails = {},
        toServiceGroupList = {},
        toCreateServiceGroup = {}
    )
}