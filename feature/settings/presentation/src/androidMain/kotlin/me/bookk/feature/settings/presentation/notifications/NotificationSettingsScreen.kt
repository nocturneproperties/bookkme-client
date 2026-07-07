package me.bookk.feature.settings.presentation.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.settings.presentation.navigation.LocalNavigation

@Composable
internal fun NotificationSettingsScreen(state: NotificationSettingsState) {
    ObserveNotifications(state = state.notifications)
    Scaffold(
        topBar = {
            AppTopBar(
                state = state.appBar,
                onNavigationIconClick = LocalNavigation.current.navigateBack
            )
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {}
        }
    )
}
