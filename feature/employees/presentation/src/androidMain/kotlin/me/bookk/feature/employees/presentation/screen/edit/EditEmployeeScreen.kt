package me.bookk.feature.employees.presentation.screen.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.employees.resources.EmployeesRes
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.InfoSection
import me.bookk.designsystem.components.OptionsMultiPicker
import me.bookk.designsystem.components.ScheduleSection
import me.bookk.designsystem.components.StateSwitch
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.modifier.topShadow
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun EditEmployeeScreen(state: EditEmployeeState) {
    Scaffold(
        topBar = {
            AppTopBar(
                state = state.appBar,
                actions = { StateTextButton(state = state.save) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .topShadow(24.dp, colorFrom = LocalColors.current.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AppCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(bottom = 8.dp)) {
                    state.contacts.items.forEach { InfoSection(it) }
                }
            }
            OptionsMultiPicker(state.services) { item, onItemRemove ->
                ServiceItem(item, onItemRemove)
            }
            ScheduleSection(state.schedule)
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Header(EmployeesRes.strings.employees_edit_permissions.desc().localized())
                state.permissionsHint?.let { PermissionsHint(it.localized()) }
                if (state.isPermissionsVisible) {
                    state.permissions.items.forEach { ResourcePermissionCard(it) }
                }
            }
        }
    }
}

@Composable
private fun PermissionsHint(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp),
        style = MaterialTheme.typography.bodySmall.secondary()
    )
}

@Composable
private fun ResourcePermissionCard(state: ResourcePermissionState) {
    Column {
        Header(state.title.localized())
        AppCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(vertical = 8.dp)) {
                StateSwitch(state.canView)
                StateSwitch(state.canUpdate)
                StateSwitch(state.canDelete)
            }
        }
    }
}

@Composable
private fun ServiceItem(
    service: EmployeeServicePresentation,
    onItemRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.padding(start = 16.dp)) {
            Text(
                service.displayName.localized(),
                style = MaterialTheme.typography.titleMedium.primary()
            )
            Text(
                service.duration.localized(),
                style = MaterialTheme.typography.labelMedium.secondary()
            )
        }
        Spacer(Modifier.weight(1f))
        Text(service.price, style = MaterialTheme.typography.titleMedium.primary())
        TextButton(onClick = onItemRemove) {
            Box(
                modifier = Modifier
                    .background(AppColors.White.copy(alpha = 0.1f), CircleShape)
                    .size(28.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "-",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium.secondary()
                )
            }
        }
    }
}
