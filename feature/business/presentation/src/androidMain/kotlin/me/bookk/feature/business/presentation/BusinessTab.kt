package me.bookk.feature.business.presentation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.bootstrap.BusinessBootstrapViewModel
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.businessGraph
import org.koin.androidx.compose.koinViewModel
import kotlin.uuid.Uuid

@Composable
fun BusinessTab(
    showClients: (id: Uuid) -> Unit
) {
    val businessController = rememberNavController()
    val viewModel: BusinessBootstrapViewModel = koinViewModel()
    ObserveNotifications(viewModel.uiState.notification)
    NavHost(
        modifier = Modifier.fillMaxSize(),
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
                toClients = { showClients(it) },
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