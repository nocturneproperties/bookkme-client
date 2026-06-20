package me.bookk.feature.settings.presentation.accdelete

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.components.StateSwitch
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.feature.settings.presentation.navigation.LocalNavigation

@Composable
internal fun DeleteAccountScreen(state: DeleteAccountState) {
    ObserveNotifications(state.notifications)
    Scaffold(
        topBar = {
            AppTopBar(
                state = state.appBar,
                onNavigationIconClick = LocalNavigation.current.navigateBack
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .padding(it)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    modifier = Modifier.padding(top = 24.dp),
                    text = state.confirmationMessage.localized(),
                    style = MaterialTheme.typography.bodyLarge
                )
                AppCard {
                    StateSwitch(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        state = state.confirmation,
                        onCheckedChange = LocalDeleteAccountEventListener.current.onSwitchStateChanged
                    )
                }

            }
        },
        bottomBar = {
            ActionButton(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                state = state.deleteButton,
                onClick = LocalDeleteAccountEventListener.current.onDeleteClick
            )
        }
    )
}


@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        DeleteAccountScreen(
            state = AndroidDeleteAccountState(DeleteAccountViewModel.createInitData())
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        DeleteAccountScreen(
            state = AndroidDeleteAccountState(DeleteAccountViewModel.createInitData())
        )
    }
}