package me.bookk.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.bookk.designsystem.theme.AppTheme
import me.bookk.feature.authorization.presentation.navigation.SignInDestination
import me.bookk.feature.authorization.presentation.navigation.authGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationRoot()
                }
            }
        }
    }
}

@Composable
fun NavigationRoot() {
    val controller = rememberNavController()
    NavHost(
        navController = controller,
        startDestination = SignInDestination.route
    ) {
        authGraph(
            navigateToMainScreen = {}
        )
    }
}
