package me.bookk.feature.settings.presentation.contactus

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
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.components.StateSwitch
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.settings.presentation.navigation.LocalNavigation

@Composable
internal fun ContactUsScreen(state: ContactUsState) {
    ObserveNotifications(state.notifications)
    ObserveNavigation(state.navigation) {
        when (it) {
            ContactUsNavigationDestination.Back -> LocalNavigation.current.navigateBack()
        }
    }
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
                    .padding(horizontal = 16.dp)
            ) {
                TextField(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    state = state.contactField,
                    minLines = 4,
                    onValueChange = LocalContactUsEventListener.current.onTextChanged
                )

                StateSwitch(
                    modifier = Modifier.padding(top = 16.dp),
                    state = state.includeLogsSwitch,
                    onCheckedChange = LocalContactUsEventListener.current.onIncludeUsageLogsCheckedChanged
                )

                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = state.logsExplanationText.localized(),
                    style = MaterialTheme.typography.labelMedium,
                    color = LocalColors.current.secondaryText
                )
            }
        },
        bottomBar = {
            ActionButton(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                state = state.submitButton,
                onClick = LocalContactUsEventListener.current.onSubmitClick
            )
        }
    )
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        ContactUsScreen(
            state = AndroidContactUsState(ContactUsViewModel.createInitData())
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        ContactUsScreen(
            state = AndroidContactUsState(ContactUsViewModel.createInitData())
        )
    }
}