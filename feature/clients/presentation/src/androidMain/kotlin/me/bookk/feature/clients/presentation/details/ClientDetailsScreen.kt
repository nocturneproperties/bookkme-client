package me.bookk.feature.clients.presentation.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.InfoSection
import me.bookk.designsystem.components.List

@Composable
internal fun ClientDetailsScreen(state: ClientDetailsState) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AppTopBar(state = state.appBar)
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .padding(top = 16.dp)
                    .fillMaxSize(),
            ) {
                List(state.infoSections) {
                    InfoSection(it)
                }
            }
        }
    )
}