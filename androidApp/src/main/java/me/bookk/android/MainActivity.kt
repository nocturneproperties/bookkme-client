package me.bookk.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.bookk.core.presentation.LocalUnauthorizedHandler
import me.bookk.core.presentation.UnauthorizedHandler
import me.bookk.designsystem.action.isKeyboardMovingDownOrInvisible
import me.bookk.designsystem.action.keyboardMovingDirection
import me.bookk.designsystem.components.DefaultSnackbarProvider
import me.bookk.designsystem.components.LocalSnackbarProvider
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapViewModel
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.navigation.AuthDestination
import me.bookk.feature.authorization.presentation.navigation.AuthNavigation
import me.bookk.feature.authorization.presentation.navigation.authGraph
import me.bookk.feature.business.presentation.BusinessTab
import me.bookk.feature.dashboard.presentation.navigation.DashboardDestination
import me.bookk.feature.dashboard.presentation.navigation.dashboardGraph
import me.bookk.feature.settings.presentation.SettingsTab
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
                        NavigationRoot(
                            state = viewModel.state,
                            onUnauthorized = viewModel::logOut
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun NavigationRoot(state: BootstrapState, onUnauthorized: UnauthorizedHandler) {
    val controller = rememberNavController()
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
        val snackBarState = remember { SnackbarHostState() }
        val snackBarScope = rememberCoroutineScope()
        val snackbarProvider = remember { DefaultSnackbarProvider(snackBarScope, snackBarState) }

        CompositionLocalProvider(
            LocalUnauthorizedHandler provides onUnauthorized,
            LocalSnackbarProvider provides snackbarProvider
        ) {
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
                    businessTab = { BusinessTab() },
                    settingsTab = { SettingsTab() }
                )
            }
        }
        Box(
            modifier = Modifier
                .imePadding()
                .systemBarsPadding(),
            contentAlignment = Alignment.BottomCenter
        ) {
            val direction by keyboardMovingDirection()
            val animatedPadding by animateDpAsState(if (isKeyboardMovingDownOrInvisible(direction)) 80.dp else 0.dp)

            SnackbarHost(
                modifier = Modifier.padding(bottom = animatedPadding),
                hostState = snackBarState
            ) {
                Snackbar(
                    snackbarData = it,
                    containerColor = LocalColors.current.secondaryText,
                    contentColor = LocalColors.current.background
                )
            }
        }
    }
}
