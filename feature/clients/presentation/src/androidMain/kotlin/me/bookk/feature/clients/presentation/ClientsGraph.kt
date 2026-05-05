package me.bookk.feature.clients.presentation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.clients.presentation.list.clientsListScreen

fun NavGraphBuilder.clientsGraph(navigation: ClientsNavigation) {
    clientsListScreen(navigation)
}