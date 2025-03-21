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
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.settings.presentation.dashboard.LocalDashboardEventListener
import me.bookk.feature.settings.presentation.dashboard.SectionItem
import me.bookk.feature.settings.presentation.dashboard.SupportSection
import me.bookk.feature.settings.presentation.navigation.LocalNavigation

@Composable
internal fun SupportSection(state: SupportSection) {
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

        Column(modifier = Modifier.background(LocalColors.current.elevated, shape = MaterialTheme.shapes.medium)) {
            SectionItem(
                text = state.contact.text.localized(),
                onClick = LocalNavigation.current.navigateToContact
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SectionItem(
                text = state.terms.text.localized(),
                onClick = LocalDashboardEventListener.current.showTerms
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SectionItem(
                text = state.policy.text.localized(),
                onClick = LocalDashboardEventListener.current.showPolicy
            )
            HorizontalDivider(Modifier.padding(horizontal = 16.dp))
            SectionItem(
                text = state.reportError.text.localized(),
                color = LocalColors.current.error,
                onClick = LocalNavigation.current.navigateToDeleteAccount
            )
        }
    }
}