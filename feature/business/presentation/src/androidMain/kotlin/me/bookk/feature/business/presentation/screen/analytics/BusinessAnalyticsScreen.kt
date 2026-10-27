package me.bookk.feature.business.presentation.screen.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun BusinessAnalyticsScreen(
    state: BusinessAnalyticsState
) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = { AppTopBar(state = state.appBar) },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier
                        .background(
                            LocalColors.current.actionText.copy(alpha = 0.1f),
                            MaterialTheme.shapes.large
                        )
                        .size(72.dp)
                        .padding(16.dp),
                    imageVector = Icons.Outlined.Insights,
                    contentDescription = null,
                    tint = LocalColors.current.actionText
                )
                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = state.title.localized(),
                    style = MaterialTheme.typography.titleLarge.primary(),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = state.message.localized(),
                    style = MaterialTheme.typography.bodyMedium.secondary(),
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        BusinessAnalyticsScreen(state = previewState())
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        BusinessAnalyticsScreen(state = previewState())
    }
}

private fun previewState(): BusinessAnalyticsState {
    return AndroidBusinessAnalyticsState().apply {
        appBar.title = BusinessRes.strings.business_dashboard_analytics.desc()
        appBar.onBackClick = {}
        title = BusinessRes.strings.business_analytics_coming_soon_title.desc()
        message = BusinessRes.strings.business_analytics_coming_soon_message.desc()
    }
}
