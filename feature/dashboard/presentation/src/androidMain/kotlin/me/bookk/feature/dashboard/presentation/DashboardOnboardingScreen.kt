package me.bookk.feature.dashboard.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.feature.dashboard.presentation.state.AndroidOnboardingState
import me.bookk.feature.dashboard.presentation.state.OnboardingState

@Composable
internal fun DashboardOnboardingScreen(state: OnboardingState) {
    OnboardingLayout(
        refresh = state.refresh,
        headline = DashboardRes.strings.dashboard_onboarding_headline.desc().localized(),
        subtitle = DashboardRes.strings.dashboard_onboarding_subtitle.desc().localized()
    ) {
        CreateOrJoinBusinessActions(state)
    }
}

@Composable
internal fun DashboardBusinessSelectionScreen(state: OnboardingState) {
    OnboardingLayout(
        refresh = state.refresh,
        headline = DashboardRes.strings.dashboard_onboarding_select_headline.desc().localized(),
        subtitle = DashboardRes.strings.dashboard_onboarding_select_subtitle.desc().localized()
    ) {
        state.businesses.forEach { business ->
            AppCard(modifier = Modifier.fillMaxWidth()) {
                OnboardingActionRow(
                    icon = Icons.Filled.Business,
                    title = business.name,
                    onClick = { state.onBusinessClick?.invoke(business) }
                )
            }
        }
        Text(
            text = DashboardRes.strings.dashboard_onboarding_select_or.desc().localized(),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = LocalColors.current.secondaryText,
            textAlign = TextAlign.Center
        )
        CreateOrJoinBusinessActions(state)
    }
}

@Composable
private fun CreateOrJoinBusinessActions(state: OnboardingState) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        OnboardingActionRow(
            icon = Icons.Filled.Storefront,
            title = DashboardRes.strings.dashboard_onboarding_create_title.desc().localized(),
            subtitle = DashboardRes.strings.dashboard_onboarding_create_subtitle.desc().localized(),
            onClick = { state.onCreateBusinessClick?.invoke() }
        )
    }
    AppCard(modifier = Modifier.fillMaxWidth()) {
        OnboardingActionRow(
            icon = Icons.Filled.GroupAdd,
            title = DashboardRes.strings.dashboard_onboarding_join_title.desc().localized(),
            subtitle = DashboardRes.strings.dashboard_onboarding_join_subtitle.desc().localized(),
            onClick = { state.onJoinBusinessClick?.invoke() }
        )
    }
}

@Composable
internal fun DashboardSetupRequiredScreen(state: OnboardingState) {
    OnboardingLayout(
        refresh = state.refresh,
        headline = DashboardRes.strings.dashboard_onboarding_setup_headline.desc().localized(),
        subtitle = DashboardRes.strings.dashboard_onboarding_setup_subtitle.desc().localized()
    ) {
        OnboardingStepCard(
            icon = Icons.Filled.Category,
            title = DashboardRes.strings.dashboard_onboarding_group_title.desc().localized(),
            subtitle = state.serviceGroupStepSubtitle.localized(),
            isDone = state.isServiceGroupStepDone,
            isEnabled = state.isServiceGroupStepEnabled,
            onClick = { state.onAddServiceGroupClick?.invoke() }
        )
        OnboardingStepCard(
            icon = Icons.Filled.DesignServices,
            title = DashboardRes.strings.dashboard_onboarding_service_title.desc().localized(),
            subtitle = state.serviceStepSubtitle.localized(),
            isDone = state.isServiceStepDone,
            isEnabled = state.isServiceStepEnabled,
            onClick = { state.onAddServiceClick?.invoke() }
        )
        OnboardingStepCard(
            icon = Icons.Filled.PersonAdd,
            title = DashboardRes.strings.dashboard_onboarding_client_title.desc().localized(),
            subtitle = state.clientStepSubtitle.localized(),
            isDone = state.isClientStepDone,
            isEnabled = state.isClientStepEnabled,
            onClick = { state.onAddClientClick?.invoke() }
        )
        OnboardingStepCard(
            icon = Icons.Filled.Extension,
            title = DashboardRes.strings.dashboard_onboarding_plugins_title.desc().localized(),
            subtitle = DashboardRes.strings.dashboard_onboarding_plugins_subtitle.desc().localized(),
            isDone = state.isAppointmentsStepDone,
            isEnabled = state.isAppointmentsStepEnabled,
            onClick = { state.onEnablePluginsClick?.invoke() }
        )
    }
}

@Composable
private fun OnboardingStepCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isDone: Boolean,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (isEnabled || isDone) 1f else 0.5f)
    ) {
        OnboardingActionRow(
            icon = icon,
            title = title,
            subtitle = subtitle,
            isDone = isDone,
            isClickable = isEnabled && !isDone,
            onClick = onClick
        )
    }
}

@Composable
internal fun DashboardAwaitingSetupScreen(state: OnboardingState) {
    OnboardingLayout(
        refresh = state.refresh,
        headline = DashboardRes.strings.dashboard_onboarding_awaiting_headline.desc().localized(),
        subtitle = state.awaitingSetupMessage.localized()
    ) {}
}

@Composable
private fun OnboardingLayout(
    refresh: RefreshState,
    headline: String,
    subtitle: String,
    actions: @Composable ColumnScope.() -> Unit
) {
    PullToRefresh(state = refresh, modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            OnboardingContent(headline = headline, subtitle = subtitle, actions = actions)
            Spacer(modifier = Modifier.weight(2f))
        }
    }
}

@Composable
private fun OnboardingContent(
    headline: String,
    subtitle: String,
    actions: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.titleMedium,
                color = LocalColors.current.primaryText,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = LocalColors.current.secondaryText,
                textAlign = TextAlign.Center
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), content = actions)
    }
}

@Composable
private fun OnboardingActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    isDone: Boolean = false,
    isClickable: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isClickable, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LocalColors.current.actionText,
            modifier = Modifier.size(24.dp)
        )
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (subtitle == null) {
                OnboardingActionTexts(
                    title = title,
                    subtitle = " ",
                    modifier = Modifier
                        .alpha(0f)
                        .clearAndSetSemantics {}
                )
                OnboardingActionTitle(title)
            } else {
                OnboardingActionTexts(title = title, subtitle = subtitle)
            }
        }
        if (isDone) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = LocalColors.current.success,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = LocalColors.current.secondaryText,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun OnboardingActionTexts(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        OnboardingActionTitle(title)
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = LocalColors.current.secondaryText
        )
    }
}

@Composable
private fun OnboardingActionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = LocalColors.current.primaryText
    )
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
private fun PreviewBusinessSelection() {
    AppTheme(themeMode = ThemeMode.DARK) {
        DashboardBusinessSelectionScreen(state = AndroidOnboardingState())
    }
}

@Preview
@Composable
private fun PreviewSetupRequired() {
    AppTheme(themeMode = ThemeMode.DARK) {
        DashboardSetupRequiredScreen(state = AndroidOnboardingState())
    }
}

@Preview
@Composable
private fun PreviewAwaitingSetup() {
    AppTheme(themeMode = ThemeMode.DARK) {
        DashboardAwaitingSetupScreen(state = AndroidOnboardingState())
    }
}
