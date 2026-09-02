package me.bookk.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import me.bookk.feature.clients.presentation.ClientsDestinations
import me.bookk.feature.clients.presentation.ClientsNavigation
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun rememberClientsNavigation(controller: NavController) = remember(controller) {
    ClientsNavigation(
        onBack = { controller.popBackStack() },
        toAddClient = { controller.navigate(ClientsDestinations.CreateClient(it)) },
        toClientDetails = { controller.navigate(ClientsDestinations.ClientDetails(it)) },
        toEditClient = { controller.navigate(ClientsDestinations.EditClient(it)) }
    )
}