package me.bookk.feature.settings.presentation.editprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.settings.presentation.navigation.LocalNavigation

@Composable
internal fun EditProfileScreen(state: EditProfileState) {
    ObserveNotifications(state = state.notification)
    HandleNavigation(state.navigation)
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
                    .padding(16.dp)
                    .padding(top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    state = state.name,
                    onValueChange = LocalEditProfileEventListener.current.onNameChanged
                )
                TextField(
                    state = state.lastName,
                    onValueChange = LocalEditProfileEventListener.current.onLastNameChanged
                )
                TextField(
                    state = state.email,
                    onValueChange = LocalEditProfileEventListener.current.onEmailChanged
                )
            }
        },
        bottomBar = {
            ActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                state = state.confirmButton,
                onClick = LocalEditProfileEventListener.current.onSaveClick
            )
        }
    )
}

@Composable
private fun HandleNavigation(navigation: NavigationState<EditProfileNavigationDestination>) {
    ObserveNavigation(navigation) {
        when (it) {
            EditProfileNavigationDestination.Back -> LocalNavigation.current.navigateBack()
        }
    }
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        EditProfileScreen(
            state = AndroidEditProfileState(EditProfileViewModel.createInitData())
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        EditProfileScreen(
            state = AndroidEditProfileState(EditProfileViewModel.createInitData())
        )
    }
}