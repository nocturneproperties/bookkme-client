package me.bookk.android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import me.bookk.feature.authorization.presentation.navigation.AuthDestination
import me.bookk.feature.authorization.presentation.navigation.AuthNavigation
import me.bookk.feature.dashboard.presentation.navigation.DashboardDestination

@Composable
fun rememberAuthNavigation(controller: NavController) = remember {
    AuthNavigation(
        navigateBack = controller::popBackStack,
        navigateToMainScreen = {
            controller.navigate(DashboardDestination) { popUpTo(0) }
        },
        navigateToSignUp = { controller.navigate(AuthDestination.SignUp) },
        navigateToTroubleshoot = { controller.navigate(AuthDestination.Troubleshoot) },
        navigateToContactSupport = {}
    )
}