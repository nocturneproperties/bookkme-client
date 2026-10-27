package me.bookk.feature.authorization.presentation.sign_in

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.authorization.presentation.navigation.LocalNavigation
import me.bookk.feature.authorization.presentation.sign_in.state.SignInEventListener
import me.bookk.feature.authorization.presentation.sign_in.state.SignInState

@Composable
fun SignInScreen(
    state: SignInState,
    listener: SignInEventListener
) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AppTopBar(state = state.appBar)
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddings)
                    .padding(all = 16.dp)
            ) {
                PasskeyInfoCard(
                    state = state,
                    onButtonClick = listener::onLearnMoreClick
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.signInButton,
                    onClick = listener::onSignInClick
                )
                StateTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.signUpButton,
                    onClick = LocalNavigation.current.navigateToSignUp
                )
                StateTextButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.troubleshootButton,
                    onClick = LocalNavigation.current.navigateToTroubleshoot
                )
            }
        }
    )
}

@Composable
private fun PasskeyInfoCard(state: SignInState, onButtonClick: () -> Unit) {
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
            StateTextButton(
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
        SignInScreen(
            state = AndroidSignInState(SignInViewModel.createInitData()),
            listener = mockListener()
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        SignInScreen(
            state = AndroidSignInState(SignInViewModel.createInitData()),
            listener = mockListener()
        )
    }
}

private fun mockListener() = object : SignInEventListener {
    override fun onBackClick() {
    }

    override fun onSignInClick() {
    }

    override fun onLearnMoreClick() {
    }
}