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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.CheckBox
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.feature.authorization.presentation.sign_up.state.SignUpState

@Composable
fun SignUpScreen(state: SignUpState) {
    ObserveNotifications(state = state.notification)
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AppTopBar(
                state = state.appBar,
            )
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddings)
                    .padding(all = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextField(
                    state = state.name,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                    ),
                )
                TextField(
                    state = state.lastName,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                    ),
                )
                TextField(
                    state = state.email,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Email,
                    ),
                )
                Spacer(Modifier.height(24.dp))
                CheckBox(
                    state.legalAcceptance,
                    text = legalAcceptanceText(state),
                    textColor = LocalColors.current.secondaryText,
                    textSize = 12.sp,
                    textLineHeight = 16.sp,
                )
                if (state.reloadLegalDocuments.isVisible) {
                    StateTextButton(state.reloadLegalDocuments)
                }
                PasskeyInfoCard(state)
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            ) {
                ActionButton(
                    modifier = Modifier.fillMaxWidth(),
                    state = state.confirmButton,
                )
            }
        },
    )
}

@Composable
private fun legalAcceptanceText(state: SignUpState): AnnotatedString {
    val text = state.legalAcceptance.text.localized()
    val documents = state.legalDocuments
    val links = listOfNotNull(documents?.privacyPolicy, documents?.termsOfService)
        .map { it to it.linkText.localized() }
    val linkColor = LocalColors.current.actionText
    return buildAnnotatedString {
        append(text)
        links.forEach { (document, linkText) ->
            val linkStart = text.indexOf(linkText)
            if (linkStart >= 0 && linkText.isNotEmpty()) {
                addLink(
                    LinkAnnotation.Url(
                        url = document.linkUrl,
                        styles = TextLinkStyles(
                            style = SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                        ),
                        linkInteractionListener = { document.onLinkClick?.invoke() },
                    ),
                    start = linkStart,
                    end = linkStart + linkText.length,
                )
            }
        }
    }
}

@Composable
private fun PasskeyInfoCard(state: SignUpState) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column {
            Text(
                modifier = Modifier.padding(
                    start = 24.dp,
                    top = 24.dp,
                    end = 24.dp,
                    bottom = 16.dp,
                ),
                style = MaterialTheme.typography.titleMedium,
                text = state.passkeyInfoCardData.title.localized(),
            )
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.bodySmall,
                text = state.passkeyInfoCardData.description.localized(),
                color = LocalColors.current.secondaryText,
            )
            StateTextButton(
                modifier = Modifier.padding(start = 8.dp),
                state = state.learnMoreButton,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        SignUpScreen(
            state = AndroidSignUpState(),
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        SignUpScreen(
            state = AndroidSignUpState(),
        )
    }
}
