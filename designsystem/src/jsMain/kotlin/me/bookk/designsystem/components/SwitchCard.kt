package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.overflowWrap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodySmallTextStyle
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.uistate.BooleanState
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private const val SWITCH_ROW_HORIZONTAL_PADDING_REM = 1.0

@Composable
fun SwitchCard(states: List<BooleanState>, modifier: Modifier = Modifier) {
    val shown = states.filter { it.isVisible }
    if (shown.isEmpty()) return
    AppCard(
        Modifier
            .fillMaxWidth()
            .padding(0.px)
            .gap(0.px)
            .then(modifier),
    ) {
        shown.forEachIndexed { index, state ->
            key(state.id) { SwitchRow(state) }
            if (index != shown.lastIndex) SwitchDivider()
        }
    }
}

@Composable
private fun SwitchRow(state: BooleanState) {
    Row(
        Modifier
            .fillMaxWidth()
            .boxSizing(BoxSizing.BorderBox)
            .minHeight(3.5.cssRem)
            .gap(0.75.cssRem)
            .padding(
                leftRight = SWITCH_ROW_HORIZONTAL_PADDING_REM.cssRem,
                topBottom = 0.625.cssRem,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .flexGrow(1)
                .minWidth(0.px)
                .gap(0.125.cssRem)
                .overflowWrap(OverflowWrap.Anywhere),
        ) {
            TextBlock(
                state.text,
                BodyLargeTextStyle
                    .toModifier()
                    .margin(0.px),
            )
            state.supportingTextRes?.let {
                TextBlock(
                    it,
                    BodySmallTextStyle
                        .toModifier()
                        .margin(0.px)
                        .secondary(),
                )
            }
        }
        StateToggle(state)
    }
}

@Composable
private fun SwitchDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .boxSizing(BoxSizing.BorderBox)
            .padding(left = SWITCH_ROW_HORIZONTAL_PADDING_REM.cssRem),
    ) {
        HorizontalDivider()
    }
}
