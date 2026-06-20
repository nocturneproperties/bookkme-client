package me.bookk.feature.business.presentation.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.SectionItem
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardSection
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardState
import kotlin.uuid.Uuid

@Composable
internal fun DashboardScreen(
    state: BusinessDashboardState
) {
    Scaffold(
        topBar = { AppTopBar(state = state.appBar) },
        content = {
            LazyColumn(
                modifier = Modifier.padding(it),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.sections) { item ->
                    DashboardSection(item)
                }
            }
        }
    )
}

@Composable
private fun DashboardSection(item: BusinessDashboardSection) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = item.title.localized(),
            style = MaterialTheme.typography.titleMedium,
            color = LocalColors.current.header
        )

        Column(
            modifier = Modifier.background(
                LocalColors.current.elevated,
                shape = MaterialTheme.shapes.medium
            )
        ) {
            val clickListener = LocalDashboardEventListener.current.onItemClicked
            item.items.forEachIndexed { index, dashboardUIItem ->
                SectionItem(
                    text = dashboardUIItem.text.localized(),
                    onClick = { clickListener(dashboardUIItem) }
                )
                if (index != item.items.size - 1) {
                    HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        DashboardScreen(
            state = AndroidDashboardState().apply {
                appBar.title = "Business name".desc()
                updateSections(listOf(BusinessDashboardSection.Business(Uuid.random()), BusinessDashboardSection.Appointments(Uuid.random()), BusinessDashboardSection.Shop()))
            }
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        DashboardScreen(
            state = AndroidDashboardState().apply {
                appBar.title = "Business name".desc()
                updateSections(listOf(BusinessDashboardSection.Business(Uuid.random()), BusinessDashboardSection.Appointments(Uuid.random()), BusinessDashboardSection.Shop()))
            }
        )
    }
}