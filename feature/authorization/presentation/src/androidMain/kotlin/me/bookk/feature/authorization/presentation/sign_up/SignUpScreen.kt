package me.bookk.feature.authorization.presentation.sign_up

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import me.bookk.designsystem.theme.AppTheme
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpEventListener
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

@Composable
fun SignUpScreen(
    state: SignUpState,
    listener: SignUpEventListener
) {
}

@Preview
@Composable
private fun Preview() {
    AppTheme {
        SignUpScreen(
            state = SignUpStateImpl(SignUpViewModel.createInitData()),
            listener = object : SignUpEventListener {

            }
        )
    }
}