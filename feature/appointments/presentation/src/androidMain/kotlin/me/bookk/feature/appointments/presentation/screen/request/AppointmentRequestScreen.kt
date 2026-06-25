package me.bookk.feature.appointments.presentation.screen.request

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.bookk.designsystem.components.AppTopBar

@Composable
internal fun AppointmentRequestScreen(
    state: AppointmentRequestState
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { AppTopBar(state = state.appBar) }
    ) {
        Column(Modifier.padding(it)) {

        }
    }
}
