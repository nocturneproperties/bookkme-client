package me.bookk.feature.authorization.presentation.sign_up

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.authorization.presentation.navigation.LocalNavigation
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpEventListener
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

@Composable
fun SignUpScreen(
    state: SignUpState,
    listener: SignUpEventListener
) {
    ObserveNotifications(state = state.notification)
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AppTopBar(
                state = state.appBar,
                onNavigationIconClick = LocalNavigation.current.navigateBack
            )
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddings)
                    .padding(all = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    state = state.name,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = { listener.onFirstNameTextChanged(it) }
                )
                TextField(
                    state = state.lastName,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text
                    ),
                    onValueChange = { listener.onLastNameTextChanged(it) }
                )
                TextField(
                    state = state.email,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Email
                    ),
                    onValueChange = { listener.onEmailTextChanged(it) }
                )
                Spacer(Modifier.height(24.dp))
                PasskeyInfoCard(state, listener::onLearnMoreClick)
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.confirmButton,
                    onClick = listener::onConfirmButtonClick
                )
            }
        }
    )
}

@Composable
private fun PasskeyInfoCard(state: SignUpState, onButtonClick: () -> Unit) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            Text(
                modifier = Modifier.padding(
                    start = 24.dp,
                    top = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp
                ),
                style = MaterialTheme.typography.titleMedium,
                text = state.passkeyInfoCardData.title.localized()
            )
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.bodySmall,
                text = state.passkeyInfoCardData.description.localized(),
                color = LocalColors.current.secondaryText
            )
            TextButton(
                modifier = Modifier.padding(start = 8.dp),
                state = state.learnMoreButton,
                onClick = onButtonClick
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        SignUpScreen(
            state = AndroidSignUpState(SignUpViewModel.createInitData()),
            listener = mockListener()
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        SignUpScreen(
            state = AndroidSignUpState(SignUpViewModel.createInitData()),
            listener = mockListener()
        )
    }
}

private fun mockListener() = object : SignUpEventListener {
    override fun onFirstNameTextChanged(text: String) {
    }

    override fun onLastNameTextChanged(text: String) {
    }

    override fun onEmailTextChanged(text: String) {
    }

    override fun onConfirmButtonClick() {
    }

    override fun onLearnMoreClick() {
    }
}