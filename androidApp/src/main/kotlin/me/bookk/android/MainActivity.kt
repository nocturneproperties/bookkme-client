package me.bookk.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import me.bookk.android.navigation.rememberAuthNavigation
import me.bookk.android.navigation.rememberClientsNavigation
import me.bookk.android.navigation.rememberServicesNavigation
import me.bookk.core.android.AndroidActivityAware
import me.bookk.core.presentation.LocalUnauthorizedHandler
import me.bookk.core.presentation.UnauthorizedHandler
import me.bookk.designsystem.action.isKeyboardMovingDownOrInvisible
import me.bookk.designsystem.action.keyboardMovingDirection
import me.bookk.designsystem.components.DefaultSnackbarProvider
import me.bookk.designsystem.components.LocalSnackbarProvider
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.appointments.presentation.AppointmentsTab
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapViewModel
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.navigation.AuthDestination
import me.bookk.feature.authorization.presentation.navigation.authGraph
import me.bookk.feature.business.presentation.BusinessTab
import me.bookk.feature.clients.presentation.ClientsDestinations
import me.bookk.feature.clients.presentation.clientsGraph
import me.bookk.feature.dashboard.presentation.navigation.DashboardDestination
import me.bookk.feature.dashboard.presentation.navigation.dashboardGraph
import me.bookk.feature.services.presentation.ServicesDestination
import me.bookk.feature.services.presentation.servicesGraph
import me.bookk.feature.settings.presentation.SettingsTab
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.mp.KoinPlatform
import kotlin.uuid.ExperimentalUuidApi

class MainActivity : ComponentActivity() {

    private val viewModel: BootstrapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.state.startDestination == null
        }
        super.onCreate(savedInstanceState)
        KoinPlatform.getKoin().getAll<AndroidActivityAware>()
            .forEach { it.attachActivity(this) }
        setContent {
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalUuidApi::class)
@Composable
private fun NavigationRoot(state: BootstrapState, onUnauthorized: UnauthorizedHandler) {
    val controller = rememberNavController()
    val authNavigation = rememberAuthNavigation(controller)
    val clientsNavigation = rememberClientsNavigation(controller)
    val servicesNavigation = rememberServicesNavigation(controller)
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
                modifier = Modifier.fillMaxSize(),
                navController = controller,
                startDestination = when (destination) {
                    BootstrapNavigationDestination.Login -> AuthDestination.SignIn
                    BootstrapNavigationDestination.Main -> DashboardDestination
                },
                enterTransition = { slideIntoContainer(SlideDirection.Start, tween(400)) },
                exitTransition = {
                    slideOutOfContainer(
                        SlideDirection.Start,
                        tween(400),
                        targetOffset = { (it * 0.2).toInt() })
                },
                popEnterTransition = {
                    slideIntoContainer(
                        SlideDirection.End,
                        tween(400),
                        initialOffset = { (it * 0.2).toInt() })
                },
                popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(400)) }
            ) {
                authGraph(navigation = authNavigation)
                clientsGraph(navigation = clientsNavigation)
                servicesGraph(navigation = servicesNavigation)
                dashboardGraph(
                    appointmentsTab = { AppointmentsTab() },
                    businessTab = {
                        BusinessTab(
                            showClients = {
                                controller.navigate(ClientsDestinations.Clients(it))
                            },
                            showServices = {
                                controller.navigate(ServicesDestination.Services(it))
                            }
                        )
                    },
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
