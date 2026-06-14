package me.bookk.feature.appointments.presentation.screen.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.bookk.designsystem.components.AppTopBar

@Composable
internal fun AppointmentCreateScreen(
    state: AppointmentCreateState
) {
    Scaffold(
        topBar = {
            AppTopBar(state = state.appBar)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // TODO: Implement screen content
        }
    }
}
