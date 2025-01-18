package me.bookk.feature.authorization.presentation.sign_in

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import me.bookk.core.presentation.string
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.components.TopBarSize
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.authorization.presentation.sign_in.state.Reason
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
                PassKeyTroubleshootCard(
                    state = state,
                    onButtonClick = listener::onLearnMoreClick
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
                    state = state.signInButton,
                    onClick = listener::onSignInClick
                )
            }
        }
    )
}

@Composable
private fun PassKeyTroubleshootCard(
    state: SignInState,
    onButtonClick: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column {
            Row(
                modifier = Modifier.padding(all = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                    text = state.passkeyCard.title.string()
                )

                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(state.passkeyCard.icon),
                        contentDescription = null
                    )
                }
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(LocalColors.current.Divider)
            )
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                state.passkeyCard.reasons.forEach {
                    ReasonItem(reason = it)
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.learnMoreButton,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onClick = onButtonClick
                )
            }
        }
    }
}

@Composable
private fun ReasonItem(reason: Reason) {
    var isExpanded by remember { mutableStateOf(false) }
    val angle: Float by animateFloatAsState(
        targetValue = if (isExpanded) 90f else 0f,
        animationSpec = tween(
            durationMillis = 200,
            easing = LinearEasing
        ),
        label = "ArrowRotationAnimation"
    )
    Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = reason.title.string(),
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                modifier = Modifier.rotate(angle),
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null
            )
        }
        AnimatedVisibility(isExpanded) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                text = reason.description.string(),
                style = MaterialTheme.typography.bodyMedium,
                color = LocalColors.current.SecondaryText
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
    override fun onSignInClick() {
    }

    override fun onLearnMoreClick() {
    }
}