package me.bookk.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import me.bookk.feature.clients.presentation.ClientsNavigation

@Composable
fun rememberClientsNavigation(controller: NavController) = remember {
    ClientsNavigation(
        onBack = { controller.popBackStack() }
    )
}