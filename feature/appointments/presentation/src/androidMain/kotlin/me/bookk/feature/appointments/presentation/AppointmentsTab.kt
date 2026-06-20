package me.bookk.feature.appointments.presentation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.bookk.feature.appointments.presentation.navigation.AppointmentNavigation
import me.bookk.feature.appointments.presentation.navigation.AppointmentsDestination
import me.bookk.feature.appointments.presentation.navigation.appointmentsGraph

@Composable
fun AppointmentsTab() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppointmentsDestination.List,
        enterTransition = { slideIntoContainer(SlideDirection.Start, tween(400)) },
        exitTransition = { scaleOut(targetScale = 0.92f) },
        popEnterTransition = { scaleIn(initialScale = 0.92f) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(400)) }
    ) {
        appointmentsGraph(
            navigation = AppointmentNavigation(
                createAppointment = { navController.navigate(AppointmentsDestination.Create(it)) },
                details = { navController.navigate(AppointmentsDestination.Details(it)) },
                appointmentSettings = { navController.navigate(AppointmentsDestination.Settings(it)) },
                onBack = { navController.popBackStack() },
            )
        )
    }
}
