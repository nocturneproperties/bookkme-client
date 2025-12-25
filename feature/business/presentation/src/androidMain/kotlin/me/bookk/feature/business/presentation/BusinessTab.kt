package me.bookk.feature.business.presentation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapViewModel
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.businessGraph
import org.koin.androidx.compose.koinViewModel

@Composable
fun BusinessTab() {
    val businessController = rememberNavController()
    val viewModel: BusinessBootstrapViewModel = koinViewModel()
    ObserveNotifications(viewModel.uiState.notification)
    NavHost(
        navController = businessController,
        startDestination = viewModel.uiState.startDestination,
        enterTransition = { slideIntoContainer(SlideDirection.Start, tween(400)) },
        exitTransition = { scaleOut(targetScale = 0.92f) },
        popEnterTransition = { scaleIn(initialScale = 0.92f) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(400)) }
    ) {
        businessGraph(
            navigation = BusinessNavigation(
                toAnalytics = { businessController.navigate(BusinessDestination.Analytics) },
                toClients = { businessController.navigate(BusinessDestination.Clients) },
                toEmployees = { businessController.navigate(BusinessDestination.Employees) },
                toBusinessSettings = { businessController.navigate(BusinessDestination.Settings(it)) },
                toAppointmentSettings = {},
                toAppointmentHistory = {},
                toAppointmentServices = {},
                toShopOrders = {},
                toShopAssortment = {},
                toShopWarehouse = {}
            )
        )
    }
}