package me.bookk.feature.authorization.presentation.sign_up

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.components.TopBarSize
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpEventListener
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

@Composable
fun SignUpScreen(
    state: SignUpState,
    listener: SignUpEventListener
) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AppTopBar(
                state = state.appBar,
                size = TopBarSize.MEDIUM
            )
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddings)
                    .padding(all = 16.dp)
            ) {
                TextField(
                    state = state.name,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    onValueChange = { listener.onFirstNameTextChanged(it) }
                )
                TextField(
                    state = state.lastName,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next, keyboardType = KeyboardType.Text),
                    onValueChange = { listener.onLastNameTextChanged(it) }
                )
                TextField(
                    state = state.email,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
                    onValueChange = { listener.onEmailTextChanged(it) }
                )
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

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        SignUpScreen(
            state = AndroidSignUpState(SignUpViewModel.createInitData()),
            listener = object : SignUpEventListener {
                override fun onFirstNameTextChanged(text: String) {
                }

                override fun onLastNameTextChanged(text: String) {
                }

                override fun onEmailTextChanged(text: String) {
                }

                override fun onConfirmButtonClick() {
                }
            }
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        SignUpScreen(
            state = AndroidSignUpState(SignUpViewModel.createInitData()),
            listener = object : SignUpEventListener {
                override fun onFirstNameTextChanged(text: String) {
                }

                override fun onLastNameTextChanged(text: String) {
                }

                override fun onEmailTextChanged(text: String) {
                }

                override fun onConfirmButtonClick() {
                }
            }
        )
    }
}