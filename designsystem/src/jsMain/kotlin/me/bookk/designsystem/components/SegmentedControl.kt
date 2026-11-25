package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import me.bookk.designsystem.uistate.RadioButtonState
import org.jetbrains.compose.web.attributes.disabled
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

val SegmentedControlStyle = CssStyle.base {
    Modifier
        .position(Position.Relative)
        .display(DisplayStyle.Grid)
        .fillMaxWidth()
        .padding(TRACK_INSET_PX.px)
        .borderRadius(9999.px)
        .backgroundColor(ColorToken.Divider.resolve(colorMode.webColors()))
}

val SegmentButtonStyle = CssStyle {
    base {
        Modifier
            .position(Position.Relative)
            .padding(topBottom = 0.5.cssRem, leftRight = 0.875.cssRem)
            .border(0.px, LineStyle.None, Colors.Transparent)
            .borderRadius(9999.px)
            .backgroundColor(Colors.Transparent)
            .color(ColorToken.SecondaryText.resolve(colorMode.webColors()))
            .fontFamily("inherit")
            .fontSize(0.9375.cssRem)
            .fontWeight(FontWeight.SemiBold)
            .textAlign(TextAlign.Center)
            .whiteSpace(WhiteSpace.NoWrap)
            .cursor(Cursor.Pointer)
    }
    cssRule("[aria-selected=\"true\"]") {
        Modifier.color(ColorToken.PrimaryText.resolve(colorMode.webColors()))
    }
}

@Composable
fun SegmentedControl(
    label: StringDesc,
    segments: List<RadioButtonState>,
    modifier: Modifier = Modifier,
) {
    val groupLabel = label.localized()
    Div(
        SegmentedControlStyle
            .toModifier()
            .styleModifier {
                property("grid-template-columns", "repeat(${segments.size}, minmax(0, 1fr))")
            }
            .attrsModifier {
                attr("role", "tablist")
                attr("aria-label", groupLabel)
            }
            .then(modifier)
            .toAttrs(),
    ) {
        SegmentIndicator(segments.size, segments.indexOfFirst { it.isSelected })
        segments.forEach { Segment(it) }
    }
}

@Composable
private fun Segment(state: RadioButtonState) {
    Button(
        SegmentButtonStyle
            .toModifier()
            .transition(
                Transition.of("color", switchTransitionDuration(), TransitionTimingFunction.Ease),
            )
            .attrsModifier {
                attr("role", "tab")
                attr("aria-selected", state.isSelected.toString())
            }
            .toAttrs {
                if (!state.isEnabled) disabled()
                onClick { state.onClick?.invoke() }
            },
    ) {
        Text(state.text.localized())
    }
}
