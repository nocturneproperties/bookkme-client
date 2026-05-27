package me.bookk.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import library.picker.PickOptionDestination
import me.bookk.feature.services.presentation.ServicesDestination
import me.bookk.feature.services.presentation.ServicesNavigation
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun rememberServicesNavigation(controller: NavController) = remember {
    ServicesNavigation(
        controller = { controller },
        onBack = { controller.popBackStack() },
        toServiceDetails = {},
        toCreateService = { controller.navigate(ServicesDestination.AddService(it)) },
        toCreateServiceGroup = {},
        toServiceGroupList = {},
        navigateToPicker = {
            controller.navigate(PickOptionDestination(it))
        }
    )
}