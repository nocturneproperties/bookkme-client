package me.bookk.feature.services.presentation

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import library.picker.PickerScreenArgs
import kotlin.uuid.Uuid

class ServicesNavigation(
    val controller: () -> NavController,
    val onBack: () -> Unit,
    val toCreateService: (Uuid) -> Unit,
    val toServiceDetails: (Uuid) -> Unit,
    val toCreateServiceGroup: (Uuid) -> Unit,
    val toServiceGroupList: (Uuid) -> Unit,
    val navigateToPicker: (PickerScreenArgs) -> Unit
)

internal val LocalNavigation = compositionLocalOf {
    ServicesNavigation(
        controller = { throw NotImplementedError() },
        onBack = {},
        toCreateService = {},
        toServiceDetails = {},
        toServiceGroupList = {},
        toCreateServiceGroup = {},
        navigateToPicker = {}
    )
}