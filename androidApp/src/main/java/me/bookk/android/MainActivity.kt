package me.bookk.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
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
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import me.bookk.feature.settings.presentation.navigation.settingsGraph
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BootstrapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.state.navigation.navigationDestination == null
        }
        super.onCreate(savedInstanceState)
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
                    NavigationRoot(viewModel.state)
                }
            }
        }
    }
}

@Composable
private fun NavigationRoot(state: BootstrapState) {
    val controller = rememberNavController()
    val destination = state.navigation.navigationDestination
    if (destination != null) {
        NavHost(
            navController = controller,
            startDestination = when (destination) {
                BootstrapNavigationDestination.ToLogin -> AuthDestination.SignIn
                BootstrapNavigationDestination.ToMain -> DashboardDestination
            }
        ) {
            authGraph(
                navigation = AuthNavigation(
                    navigateBack = controller::popBackStack,
                    navigateToMainScreen = { controller.navigate(DashboardDestination) },
                    navigateToSignUp = { controller.navigate(AuthDestination.SignUp) },
                    navigateToTroubleshoot = { controller.navigate(AuthDestination.Troubleshoot) },
                    navigateToContactSupport = {}
                )
            )
            dashboardGraph(
                appointmentsScreen = { Text("Appointments") },
                businessScreen = { Text("Business") },
                settingsScreen = {
                    val settingsController = rememberNavController()
                    NavHost(
                        navController = settingsController,
                        startDestination = SettingsDestination.Dashboard
                    ) {
                        settingsGraph(
                            navigation = SettingsNavigation(
                                navigateBack = { settingsController.popBackStack() },
                                navigateToEditProfile = { settingsController.navigate(SettingsDestination.EditProfile) },
                                navigateToPasskey = {},
                                navigateToReport = {},
                                navigateToContact = {},
                                navigateToSuggestFeature = {},
                                navigateToDeleteAccount = {}
                            )
                        )
                    }
                }
            )
        }
    }
}
