package me.bookk.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.bookk.core.presentation.LocalUnauthorizedHandler
import me.bookk.core.presentation.UnauthorizedHandler
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapViewModel
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.navigation.AuthDestination
import me.bookk.feature.authorization.presentation.navigation.AuthNavigation
import me.bookk.feature.authorization.presentation.navigation.authGraph
import me.bookk.feature.dashboard.presentation.navigation.DashboardDestination
import me.bookk.feature.dashboard.presentation.navigation.dashboardGraph
import me.bookk.feature.settings.presentation.dashboard.SettingsTab
import org.koin.androidx.compose.KoinAndroidContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BootstrapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.state.startDestination == null
        }
        super.onCreate(savedInstanceState)
        setContent {
            KoinAndroidContext {
                AppTheme(
                    themeMode = when (viewModel.state.colorScheme) {
                        BootstrapState.UIColorScheme.DARK -> ThemeMode.DARK
                        BootstrapState.UIColorScheme.LIGHT -> ThemeMode.LIGHT
                        BootstrapState.UIColorScheme.SYSTEM -> if (isSystemInDarkTheme()) {
                            ThemeMode.DARK
                        } else {
                            ThemeMode.LIGHT
                        }
                    }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NavigationRoot(viewModel.state)
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationRoot(state: BootstrapState) {
    val controller = rememberNavController()
    val unauthorizedHandler =
        remember { UnauthorizedHandler { controller.navigate(AuthDestination.SignIn) { popUpTo(0) } } }
    val authNavigation = remember {
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
    val destination = state.startDestination
    if (destination != null) {
        CompositionLocalProvider(LocalUnauthorizedHandler provides unauthorizedHandler) {
            NavHost(
                navController = controller,
                startDestination = when (destination) {
                    BootstrapNavigationDestination.Login -> AuthDestination.SignIn
                    BootstrapNavigationDestination.Main -> DashboardDestination
                },
                enterTransition = { slideIntoContainer(SlideDirection.Start, tween(400)) },
                exitTransition = { scaleOut(targetScale = 0.95f) },
                popEnterTransition = { slideIntoContainer(SlideDirection.End, tween(400)) },
                popExitTransition = { scaleOut(targetScale = 0.95f) }
            ) {
                authGraph(navigation = authNavigation)
                dashboardGraph(
                    appointmentsTab = { Text("Appointments") },
                    businessTab = { Text("Business") },
                    settingsTab = { SettingsTab() }
                )
            }
        }
    }
}
