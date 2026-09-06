package me.bookk.feature.business.presentation.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.SectionItem
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.BusinessMenuState
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.presentation.screen.create.CreateBusinessSheet
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardSection
import me.bookk.feature.business.presentation.screen.dashboard.state.BusinessDashboardState
import kotlin.uuid.Uuid

@Composable
internal fun DashboardScreen(
    state: BusinessDashboardState
) {
    Scaffold(
        topBar = {
            AppTopBar(
                state = state.appBar,
                actions = { BusinessMenuAction(state.businessMenu) }
            )
        },
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
    if (state.isCreateBusinessSheetVisible) {
        CreateBusinessSheet(onDismiss = { state.isCreateBusinessSheetVisible = false })
    }
}

@Composable
private fun BusinessMenuAction(businessMenu: BusinessMenuState) {
    var isBusinessMenuVisible by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { isBusinessMenuVisible = true }) {
            Icon(
                imageVector = Icons.Filled.SwapHoriz,
                contentDescription = BusinessRes.strings.business_dashboard_switch_action.desc().localized(),
                tint = LocalColors.current.actionText
            )
        }
        DropdownMenu(
            expanded = isBusinessMenuVisible,
            containerColor = LocalColors.current.elevated,
            onDismissRequest = { isBusinessMenuVisible = false }
        ) {
            businessMenu.items.forEach { business ->
                DropdownMenuItem(
                    text = { Text(business.name) },
                    trailingIcon = {
                        if (business.id == businessMenu.selectedBusinessId) {
                            Icon(Icons.Filled.Check, contentDescription = null)
                        }
                    },
                    onClick = {
                        isBusinessMenuVisible = false
                        businessMenu.onBusinessClick?.invoke(business.id)
                    }
                )
            }
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(BusinessRes.strings.business_dashboard_switch_create.desc().localized()) },
                onClick = {
                    isBusinessMenuVisible = false
                    businessMenu.onCreateClick?.invoke()
                }
            )
            DropdownMenuItem(
                text = { Text(BusinessRes.strings.business_dashboard_switch_join.desc().localized()) },
                onClick = {
                    isBusinessMenuVisible = false
                    businessMenu.onJoinClick?.invoke()
                }
            )
        }
    }
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
                shape = MaterialTheme.shapes.large
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
                updateSections(listOf(BusinessDashboardSection.Business(Uuid.random(), setOf(DashboardFeature.EMPLOYEES, DashboardFeature.CLIENTS, DashboardFeature.SERVICES, DashboardFeature.BUSINESS)), BusinessDashboardSection.Appointments(Uuid.random()), BusinessDashboardSection.Shop()))
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
                updateSections(listOf(BusinessDashboardSection.Business(Uuid.random(), setOf(DashboardFeature.EMPLOYEES, DashboardFeature.CLIENTS, DashboardFeature.SERVICES, DashboardFeature.BUSINESS)), BusinessDashboardSection.Appointments(Uuid.random()), BusinessDashboardSection.Shop()))
            }
        )
    }
}
