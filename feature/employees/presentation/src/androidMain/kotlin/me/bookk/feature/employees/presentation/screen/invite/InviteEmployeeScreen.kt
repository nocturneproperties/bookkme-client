package me.bookk.feature.employees.presentation.screen.invite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.bookk.designsystem.components.AppTopBar

@Composable
fun InviteEmployeeScreen(state: InviteEmployeeState) {
    Scaffold(
        topBar = { AppTopBar(state = state.appBar) },
        content = { pv ->
            Box(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
            )
        }
    )
}
