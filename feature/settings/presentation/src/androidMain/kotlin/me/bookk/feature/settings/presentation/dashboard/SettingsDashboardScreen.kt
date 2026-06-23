package me.bookk.feature.settings.presentation.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.feature.settings.presentation.dashboard.section.AccountSection
import me.bookk.feature.settings.presentation.dashboard.section.AppearanceSection
import me.bookk.feature.settings.presentation.dashboard.section.ProfileSection

@Composable
internal fun SettingsDashboardScreen(state: SettingsState) {
    ObserveNotifications(state.notification)
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = { AppTopBar(state.appBar) },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileSection(state.profile)
                AppearanceSection(state.appearance)
                AccountSection(state.account)
            }
        }
    )
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        SettingsDashboardScreen(
            state = AndroidDashboardState(SettingsDashboardViewModel.createInitData())
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        SettingsDashboardScreen(
            state = AndroidDashboardState(SettingsDashboardViewModel.createInitData())
        )
    }
}