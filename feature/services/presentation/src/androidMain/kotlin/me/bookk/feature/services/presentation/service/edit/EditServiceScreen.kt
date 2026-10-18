package me.bookk.feature.services.presentation.service.edit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.bookk.designsystem.components.AppTopBar

@Composable
internal fun EditServiceScreen(state: EditServiceState) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
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
