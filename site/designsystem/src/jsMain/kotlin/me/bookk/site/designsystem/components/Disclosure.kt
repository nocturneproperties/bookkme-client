package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.scrollMarginBlock
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.ElementBuilder
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

val DisclosureStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .padding(topBottom = 1.cssRem)
            .scrollMarginBlock(start = 5.cssRem)
            .borderTop(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
    }
    cssRule(":first-child") { Modifier.borderTop(0.px) }
}

private val details = ElementBuilder.createBuilder<HTMLElement>("details")
private val summary = ElementBuilder.createBuilder<HTMLElement>("summary")

@Composable
fun Disclosure(
    state: BooleanState,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    if (!state.isVisible) return
    TagElement(
        details,
        DisclosureStyle
            .toModifier()
            .then(modifier)
            .toAttrs {
                if (state.isChecked) attr("open", "")
                addEventListener("toggle") {
                    val isOpen = (it.target as HTMLElement).hasAttribute("open")
                    if (isOpen != state.isChecked) state.onCheckedChange?.invoke(isOpen)
                }
            },
    ) {
        DisclosureSummary(state)
        content()
    }
}

@Composable
private fun DisclosureSummary(state: BooleanState) {
    TagElement(
        summary,
        TitleMediumTextStyle
            .toModifier()
            .cursor(Cursor.Pointer)
            .toAttrs(),
    ) {
        Text(state.text.localized())
    }
}
