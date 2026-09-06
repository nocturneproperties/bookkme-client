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
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.businessGraph
import kotlin.uuid.Uuid

@Composable
fun BusinessTab(
    showEmployees: (id: Uuid) -> Unit,
    showClients: () -> Unit,
    showServices: () -> Unit,
    showAppointmentSettings: (businessId: Uuid) -> Unit,
    showAppointmentHistory: (businessId: Uuid) -> Unit
) {
    val businessController = rememberNavController()
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = businessController,
        startDestination = BusinessDestination.Dashboard,
        enterTransition = { slideIntoContainer(SlideDirection.Start, tween(400)) },
        exitTransition = { scaleOut(targetScale = 0.92f) },
        popEnterTransition = { scaleIn(initialScale = 0.92f) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(400)) }
    ) {
        businessGraph(
            navigation = BusinessNavigation(
                toAnalytics = { businessController.navigate(BusinessDestination.Analytics) },
                toClients = { showClients() },
                toEmployees = { showEmployees(it) },
                toBusinessSettings = { businessController.navigate(BusinessDestination.Settings(it)) },
                toAppointmentSettings = { showAppointmentSettings(it) },
                toAppointmentHistory = { showAppointmentHistory(it) },
                toBusinessServices = { showServices() },
                toBusinessPlugins = { businessController.navigate(BusinessDestination.Plugins(it)) },
                toShopOrders = {},
                toShopAssortment = {},
                toShopWarehouse = {},
                toAppointmentRequests = {},
                goBack = { businessController.popBackStack() }
            )
        )
    }
}