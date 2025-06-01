package me.bookk.feature.business.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.components.TopBarSize
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.feature.business.presentation.create.state.CreateBusinessState

@Composable
internal fun CreateBusinessScreen(
    state: CreateBusinessState
) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = { AppTopBar(state = state.appBar, size = TopBarSize.MEDIUM) },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 24.dp, horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TextField(
                    state = state.name,
                    onValueChange = LocalCreateBusinessEventListener.current.onNameChanged
                )
            }
        },
        bottomBar = {
            ActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                state = state.createBtn,
                onClick = LocalCreateBusinessEventListener.current.onCreateClick
            )
        }
    )
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        CreateBusinessScreen(
            state = AndroidCreateBusinessState(CreateBusinessViewModel.createInitData())
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        CreateBusinessScreen(
            state = AndroidCreateBusinessState(CreateBusinessViewModel.createInitData())
        )
    }
}