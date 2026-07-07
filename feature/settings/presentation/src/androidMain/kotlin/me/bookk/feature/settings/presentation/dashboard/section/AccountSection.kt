package me.bookk.feature.settings.presentation.dashboard.section

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.SectionItem
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.settings.presentation.dashboard.AccountSection
import me.bookk.feature.settings.presentation.dashboard.LocalDashboardEventListener
import me.bookk.feature.settings.presentation.navigation.LocalNavigation

@Composable
internal fun AccountSection(state: AccountSection) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.title.localized(),
            style = MaterialTheme.typography.titleMedium,
            color = LocalColors.current.header
        )

        Column(
            modifier = Modifier.background(
                LocalColors.current.elevated,
                shape = MaterialTheme.shapes.large
            )
        ) {
            SectionItem(
                text = state.notifications.text.localized(),
                onClick = LocalNavigation.current.navigateToNotifications
            )
            SectionItem(
                text = state.passkey.text.localized(),
                onClick = LocalNavigation.current.navigateToPasskey
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SectionItem(
                text = state.logout.text.localized(),
                onClick = LocalDashboardEventListener.current.performLogout
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SectionItem(
                text = state.deleteAccount.text.localized(),
                color = LocalColors.current.error,
                onClick = LocalNavigation.current.navigateToDeleteAccount
            )
        }
    }
}