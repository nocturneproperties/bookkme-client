package me.bookk.feature.settings.presentation.dashboard.section

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.settings.presentation.dashboard.LocalDashboardEventListener
import me.bookk.feature.settings.presentation.dashboard.state.AppearanceSection
import me.bookk.feature.settings.presentation.dashboard.state.AppearanceSection.UIColorScheme

@Composable
internal fun AppearanceSection(state: AppearanceSection) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.title.localized(),
            style = MaterialTheme.typography.titleMedium,
            color = LocalColors.current.header
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(LocalColors.current.elevated)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val scheme = state.colorScheme
            UIColorScheme.entries.forEach {
                ThemeItem(
                    scheme = it,
                    currentScheme = scheme,
                    onSchemeSelected = LocalDashboardEventListener.current.selectScheme
                )
            }
        }
    }
}

@Composable
private fun RowScope.ThemeItem(
    scheme: UIColorScheme,
    currentScheme: UIColorScheme,
    onSchemeSelected: (UIColorScheme) -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onSchemeSelected(scheme) }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(MaterialTheme.shapes.medium)
                .background(scheme.asColor())
                .border(
                    BorderStroke(
                        if (scheme == currentScheme) 4.dp else 1.dp,
                        if (scheme == currentScheme) LocalColors.current.actionText else LocalColors.current.divider
                    ),
                    MaterialTheme.shapes.medium
                )
        )
        Text(text = scheme.title.localized(), style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
private fun UIColorScheme.asColor(): Brush {
    return when (this) {
        UIColorScheme.DARK -> SolidColor(AppColors.Black)
        UIColorScheme.LIGHT -> SolidColor(AppColors.White)
        UIColorScheme.SYSTEM -> Brush.horizontalGradient(
            0f to AppColors.Black,
            0.5f to AppColors.Black,
            0.5f to AppColors.White,
            1f to AppColors.White
        )
    }
}