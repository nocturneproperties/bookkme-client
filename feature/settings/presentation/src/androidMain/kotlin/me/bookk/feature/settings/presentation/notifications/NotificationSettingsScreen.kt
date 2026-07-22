package me.bookk.feature.settings.presentation.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.components.StateSwitch
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun NotificationSettingsScreen(state: NotificationSettingsState) {
    ObserveNotifications(state = state.notifications)
    Scaffold(
        topBar = {
            AppTopBar(state = state.appBar)
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Header(text = SettingsRes.strings.settings_notifications_channels_header.desc().localized())
                if (state.emailEnabled.isVisible) {
                    StateSwitch(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        state = state.emailEnabled
                    )
                }
                if (state.pushNotificationsEnabled.isVisible) {
                    StateSwitch(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        state = state.pushNotificationsEnabled
                    )
                }
                if (state.telegramEnabled.isVisible) {
                    StateSwitch(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        state = state.telegramEnabled
                    )
                }

                Header(text = SettingsRes.strings.settings_notifications_types_header.desc().localized())
                StateSwitch(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    state = state.appointmentEnabled
                )
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    text = SettingsRes.strings.settings_notifications_types_footer.desc().localized(),
                    style = MaterialTheme.typography.bodySmall.secondary()
                )
            }
        }
    )
}
