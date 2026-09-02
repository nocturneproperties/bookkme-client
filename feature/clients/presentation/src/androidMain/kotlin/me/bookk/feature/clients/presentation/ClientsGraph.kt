package me.bookk.feature.clients.presentation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.clients.presentation.create.createClientScreen
import me.bookk.feature.clients.presentation.details.clientDetailsScreen
import me.bookk.feature.clients.presentation.edit.editClientScreen
import me.bookk.feature.clients.presentation.list.clientsListScreen

fun NavGraphBuilder.clientsGraph(navigation: ClientsNavigation) {
    clientsListScreen(navigation)
    createClientScreen(navigation)
    clientDetailsScreen(navigation)
    editClientScreen(navigation)
}