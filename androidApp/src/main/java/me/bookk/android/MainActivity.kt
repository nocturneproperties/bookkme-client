package me.bookk.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapViewModel
import me.bookk.feature.authorization.presentation.bootstrap.state.BootstrapState
import me.bookk.feature.authorization.presentation.navigation.AuthNavigation
import me.bookk.feature.authorization.presentation.navigation.SignInDestination
import me.bookk.feature.authorization.presentation.navigation.SignUpDestination
import me.bookk.feature.authorization.presentation.navigation.TroubleshootDestination
import me.bookk.feature.authorization.presentation.navigation.authGraph
import me.bookk.feature.dashboard.presentation.navigation.DashboardDestination
import me.bookk.feature.dashboard.presentation.navigation.dashboardGraph
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BootstrapViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.state.navigation.navigationDestination == null
        }
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
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
                BootstrapNavigationDestination.ToLogin -> SignInDestination.route
                BootstrapNavigationDestination.ToMain -> DashboardDestination.route
            }
        ) {
            authGraph(
                navigation = AuthNavigation(
                    navigateBack = controller::popBackStack,
                    navigateToMainScreen = {},
                    navigateToSignUp = { controller.navigate(SignUpDestination.route) },
                    navigateToTroubleshoot = { controller.navigate(TroubleshootDestination.route) },
                    navigateToContactSupport = {}
                )
            )
            dashboardGraph(
                appointmentsScreen = { Text("Appointments") },
                businessScreen = { Text("Business") },
                settingsScreen = { Text("Settings") }
            )
        }
    }
}
