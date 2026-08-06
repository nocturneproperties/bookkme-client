package me.bookk.feature.dashboard.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.dashboard.presentation.state.AndroidOnboardingState
import me.bookk.feature.dashboard.presentation.state.OnboardingState

@Composable
internal fun DashboardOnboardingScreen(state: OnboardingState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = DashboardRes.strings.dashboard_onboarding_title.desc().localized(),
            style = MaterialTheme.typography.headlineLarge,
            color = LocalColors.current.primaryText
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.large)
                    .background(LocalColors.current.elevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Storefront,
                    contentDescription = null,
                    tint = LocalColors.current.primaryText,
                    modifier = Modifier.size(32.dp)
                )
            }
            Text(
                text = DashboardRes.strings.dashboard_onboarding_headline.desc().localized(),
                style = MaterialTheme.typography.titleMedium,
                color = LocalColors.current.primaryText,
                textAlign = TextAlign.Center
            )
            Text(
                text = DashboardRes.strings.dashboard_onboarding_subtitle.desc().localized(),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalColors.current.secondaryText,
                textAlign = TextAlign.Center
            )
        }
        AppCard(modifier = Modifier.fillMaxWidth()) {
            OnboardingStepRow(
                stepNumber = 1,
                isDone = state.isBusinessStepDone,
                isLocked = false,
                title = DashboardRes.strings.dashboard_onboarding_step_business_title.desc().localized(),
                subtitle = DashboardRes.strings.dashboard_onboarding_step_business_subtitle.desc().localized(),
                onClick = { state.onCreateBusinessClick?.invoke() }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            OnboardingStepRow(
                stepNumber = 2,
                isDone = false,
                isLocked = !state.isPluginsStepUnlocked,
                title = DashboardRes.strings.dashboard_onboarding_step_plugins_title.desc().localized(),
                subtitle = DashboardRes.strings.dashboard_onboarding_step_plugins_subtitle.desc().localized(),
                onClick = { state.onEnablePluginsClick?.invoke() }
            )
        }
    }
}

@Composable
private fun OnboardingStepRow(
    stepNumber: Int,
    isDone: Boolean,
    isLocked: Boolean,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val isClickable = !isLocked && !isDone
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(if (isLocked) Color.Transparent else LocalColors.current.actionText)
                .then(
                    if (isLocked) {
                        Modifier.border(1.dp, LocalColors.current.divider, CircleShape)
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = LocalColors.current.primaryText,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isLocked) LocalColors.current.secondaryText else LocalColors.current.primaryText
                )
            }
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = if (isLocked) LocalColors.current.secondaryText else LocalColors.current.primaryText
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = LocalColors.current.secondaryText
            )
        }
        if (isLocked) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = null,
                tint = LocalColors.current.secondaryText,
                modifier = Modifier.size(20.dp)
            )
        } else if (isClickable) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = LocalColors.current.secondaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        DashboardOnboardingScreen(state = AndroidOnboardingState())
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        DashboardOnboardingScreen(state = AndroidOnboardingState())
    }
}

@Preview
@Composable
private fun PreviewUnlocked() {
    AppTheme(themeMode = ThemeMode.DARK) {
        DashboardOnboardingScreen(
            state = AndroidOnboardingState().apply {
                isBusinessStepDone = true
                isPluginsStepUnlocked = true
            }
        )
    }
}
