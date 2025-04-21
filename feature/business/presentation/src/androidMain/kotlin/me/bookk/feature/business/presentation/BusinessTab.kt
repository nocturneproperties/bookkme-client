package me.bookk.feature.business.presentation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.bookk.feature.business.presentation.bootstrap.BootstrapViewModel
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.businessGraph
import org.koin.androidx.compose.koinViewModel

@Composable
fun BusinessTab() {
    val businessController = rememberNavController()
    val viewModel: BootstrapViewModel = koinViewModel()
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
                navigateBack = { businessController.popBackStack() }
            )
        )
    }
}