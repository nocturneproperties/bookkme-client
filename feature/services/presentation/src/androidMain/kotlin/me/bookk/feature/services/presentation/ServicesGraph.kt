package me.bookk.feature.services.presentation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.services.presentation.group.list.serviceGroupListScreen
import me.bookk.feature.services.presentation.service.add.addServiceScreen
import me.bookk.feature.services.presentation.service.list.serviceListScreen

fun NavGraphBuilder.servicesGraph(navigation: ServicesNavigation) {
    serviceListScreen(navigation)
    addServiceScreen(navigation)
    serviceGroupListScreen(navigation)
}