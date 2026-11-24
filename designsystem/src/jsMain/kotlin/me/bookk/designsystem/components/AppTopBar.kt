package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.icons.ArrowBackIcon
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.core.presentation.error.ActionType
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.BodySmallTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.uistate.AppBarActionState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.WebButtonState
import org.jetbrains.compose.web.css.cssRem

@Composable
fun AppTopBar(state: AppBarState, modifier: Modifier = Modifier, showsBackButton: Boolean = true) {
    val onBackClick = state.onBackClick?.takeIf { showsBackButton }
    val actions = state.actions.items.filter { it.isVisible }
    if (!state.isVisible || isEmptySmallBar(state, onBackClick, actions)) return
    Column(
        Modifier
            .fillMaxWidth()
            .padding(1.cssRem)
            .gap(0.5.cssRem)
            .then(modifier),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .gap(0.5.cssRem),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            onBackClick?.let { IconButton(onClick = it) { ArrowBackIcon() } }
            if (state.size ==
                TopBarSize.SMALL
            ) {
                TextBlock(state.title, TitleMediumTextStyle.toModifier())
            }
            Spacer()
            actions.forEach { AppBarAction(it) }
        }
        if (state.size == TopBarSize.LARGE) LargeTitle(state)
    }
}

@Composable
private fun isEmptySmallBar(
    state: AppBarState,
    onBackClick: (() -> Unit)?,
    actions: List<AppBarActionState>,
): Boolean {
    val hasTitle = state.size == TopBarSize.LARGE || state.title.localized().isNotEmpty()
    return !hasTitle && onBackClick == null && actions.isEmpty()
}

@Composable
private fun LargeTitle(state: AppBarState) {
    TextBlock(state.title, HeadlineLargeTextStyle.toModifier())
    state.subtitle?.let {
        TextBlock(
            it,
            BodySmallTextStyle
                .toModifier()
                .secondary(),
        )
    }
}

@Composable
private fun AppBarAction(action: AppBarActionState) {
    val button = WebButtonState(
        text = action.contentDescription,
        icon = action.icon,
        isLoading = action.isLoading,
        isEnabled = action.isEnabled,
        onClick = action.onClick,
    )
    val color = if (action.type == ActionType.NEGATIVE) {
        ColorToken.Error.themed
    } else {
        ColorToken.ActionText.themed
    }
    TextButton(button, Modifier.color(color))
}
