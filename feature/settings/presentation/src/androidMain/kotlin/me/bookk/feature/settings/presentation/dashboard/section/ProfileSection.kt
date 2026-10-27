package me.bookk.feature.settings.presentation.dashboard.section

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.settings.presentation.dashboard.ProfileSection

@Composable
internal fun ProfileSection(state: ProfileSection) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.name.localized() + " " + state.lastName.localized(),
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = state.email.localized(),
            style = MaterialTheme.typography.labelLarge,
            color = LocalColors.current.secondaryText
        )
    }
}