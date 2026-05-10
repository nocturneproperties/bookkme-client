package me.bookk.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import me.bookk.feature.services.presentation.ServicesNavigation
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun rememberServicesNavigation(controller: NavController) = remember {
    ServicesNavigation(
        onBack = { controller.popBackStack() },
        toServiceDetails = {},
        toCreateService = {},
        toCreateServiceGroup = {},
        toServiceGroupList = {}
    )
}