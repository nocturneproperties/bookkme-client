package me.bookk.feature.business.presentation.screen.plugins

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.active
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.designsystem.uistate.simple.Action
import me.bookk.designsystem.uistate.simple.OptionalInfoLine
import me.bookk.designsystem.uistate.simple.optionalLine

@Composable
internal fun BusinessPluginsScreen(
    state: BusinessPluginListState
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
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BusinessPlugin(state.appointmentPlugin)
            }
        }
    )
}

@Composable
internal fun BusinessPlugin(
    state: BusinessPluginState
) {
    AppCard(onClick = { state.isExpanded = !state.isExpanded }) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .background(
                            LocalColors.current.actionText.copy(alpha = 0.1f),
                            MaterialTheme.shapes.large
                        )
                        .size(54.dp)
                        .padding(8.dp),
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = LocalColors.current.actionText
                )
                Column(
                    Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        state.title.localized(),
                        style = MaterialTheme.typography.titleLarge.primary(),
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        state.subtitle.localized(),
                        style = MaterialTheme.typography.bodySmall.secondary()
                    )
                }
                Text(
                    modifier = Modifier
                        .background(
                            color = if (state.isEnabled)
                                LocalColors.current.actionText
                            else
                                LocalColors.current.primaryText.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.large
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    text = if (state.isEnabled)
                        BusinessRes.strings.business_plugin_enabled.desc().localized()
                    else
                        BusinessRes.strings.business_plugin_disabled.desc().localized(),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (state.isEnabled) LocalColors.current.onAction else LocalColors.current.primaryText,
                    maxLines = 1
                )
            }
            AnimatedVisibility(state.isExpanded) {
                Column {
                    FeatureList(
                        modifier = Modifier.padding(top = 16.dp),
                        icon = Icons.Filled.Person,
                        title = BusinessRes.strings.business_plugins_you_can.desc().localized(),
                        features = state.youCan.items
                    )
                    FeatureList(
                        modifier = Modifier.padding(top = 8.dp),
                        icon = Icons.Filled.Group,
                        title = BusinessRes.strings.business_plugins_clients_can.desc().localized(),
                        features = state.clientCan.items
                    )
                    AnimatedVisibility(!state.isEnabled) {
                        state.demo?.let { demo ->
                            Card(
                                modifier = Modifier.padding(top = 8.dp),
                                onClick = demo.onClick,
                                colors = CardDefaults.cardColors(
                                    containerColor = LocalColors.current.primaryText.copy(0.05f)
                                )
                            ) {
                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    text = demo.title.localized(),
                                    style = MaterialTheme.typography.labelSmall.active(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        ActionButton(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            state = state.enable
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeatureList(
    modifier: Modifier,
    icon: ImageVector,
    title: String,
    features: List<OptionalInfoLine>
) {
    Column(
        modifier
            .background(
                color = LocalColors.current.primaryText.copy(alpha = 0.05f),
                shape = MaterialTheme.shapes.large
            )
            .padding(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LocalColors.current.actionText
            )
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = LocalColors.current.divider
        )
        features.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = LocalColors.current.actionText
                )
                Column {
                    Text(it.title.localized(), style = MaterialTheme.typography.bodySmall.primary())
                    it.value?.let { subtitle ->
                        Text(
                            subtitle.localized(),
                            style = MaterialTheme.typography.labelSmall.secondary()
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        BusinessPluginsScreen(
            state = AndroidBusinessPluginListState().apply {
                appBar.title = BusinessRes.strings.business_plugins_title.desc()
                appBar.onBackClick = {}
                with(appointmentPlugin) {
                    title = BusinessRes.strings.business_plugins_appointments_title.desc()
                    subtitle = BusinessRes.strings.business_plugins_appointments_subtitle.desc()
                    isExpanded = true
                    youCan.replace(
                        listOf(
                            BusinessRes.strings.business_plugins_appointments_you_item1.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item2.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item3.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item4.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item5.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item6.desc()
                                .optionalLine()
                        )
                    )

                    clientCan.replace(
                        listOf(
                            OptionalInfoLine(
                                BusinessRes.strings.business_plugins_appointments_clients_item1.desc(),
                                BusinessRes.strings.business_plugins_appointments_clients_item1_sub.desc(),
                            ),
                            OptionalInfoLine(
                                BusinessRes.strings.business_plugins_appointments_clients_item2.desc(),
                                BusinessRes.strings.business_plugins_appointments_clients_item2_sub.desc(),
                            ),
                        )
                    )

                    demo = Action(BusinessRes.strings.business_plugins_demo.desc())
                    enable.text = DesignSystem.strings.action_enable.desc()
                }
            }
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        BusinessPluginsScreen(
            state = AndroidBusinessPluginListState().apply {
                appBar.title = BusinessRes.strings.business_plugins_title.desc()
                appBar.onBackClick = {}
                with(appointmentPlugin) {
                    title = BusinessRes.strings.business_plugins_appointments_title.desc()
                    subtitle = BusinessRes.strings.business_plugins_appointments_subtitle.desc()

                    youCan.replace(
                        listOf(
                            BusinessRes.strings.business_plugins_appointments_you_item1.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item2.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item3.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item4.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item5.desc()
                                .optionalLine(),
                            BusinessRes.strings.business_plugins_appointments_you_item6.desc()
                                .optionalLine()
                        )
                    )

                    clientCan.replace(
                        listOf(
                            OptionalInfoLine(
                                BusinessRes.strings.business_plugins_appointments_clients_item1.desc(),
                                BusinessRes.strings.business_plugins_appointments_clients_item1_sub.desc(),
                            ),
                            OptionalInfoLine(
                                BusinessRes.strings.business_plugins_appointments_clients_item2.desc(),
                                BusinessRes.strings.business_plugins_appointments_clients_item2_sub.desc(),
                            ),
                        )
                    )

                    demo = Action(BusinessRes.strings.business_plugins_demo.desc())
                    enable.text = DesignSystem.strings.action_enable.desc()
                }
            }
        )
    }
}