package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.uistate.TextState
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun StateText(
    state: TextState,
    modifier: Modifier = Modifier,
    style: CssStyle<GeneralKind> = BodyLargeTextStyle,
) {
    if (!state.isVisible) return
    val weight = if (state.isHighlighted) Modifier.fontWeight(FontWeight.SemiBold) else Modifier
    TextBlock(
        state.text,
        style
            .toModifier()
            .then(weight)
            .then(modifier),
    )
}

@Composable
fun TextBlock(text: StringDesc, modifier: Modifier = Modifier) {
    P(modifier.toAttrs()) {
        Text(text.localized())
    }
}
