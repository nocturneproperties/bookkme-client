package me.bookk.feature.business.presentation.dashboard

import androidx.compose.material3.Text
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.feature.business.presentation.navigation.BusinessDestination

internal fun NavGraphBuilder.dashboardScreen() {
    composable<BusinessDestination.Dashboard> {
        Text("Dashboard")
    }
}