package me.bookk.feature.appointments.presentation.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.StateSwitch
import me.bookk.designsystem.components.TextField

@Composable
internal fun AppointmentSettingsScreen(
    state: AppointmentSettingsState
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { AppTopBar(state = state.appBar) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RequestsSettings(state)

            ActionButton(
                state.save,
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun RequestsSettings(state: AppointmentSettingsState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Header(AppointmentsRes.strings.appointments_settings_requests.desc().localized())
            StateSwitch(state.automaticApproval, modifier = Modifier.fillMaxWidth())
        }
        TextField(state.minimalBreak)
        Column {
            Header(AppointmentsRes.strings.appointments_settings_note_header.desc().localized())
            TextField(state.note, minLines = 3)
        }
    }
}
