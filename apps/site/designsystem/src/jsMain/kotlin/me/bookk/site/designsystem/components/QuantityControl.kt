package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.Transition
import com.varabyte.kobweb.compose.css.TransitionTimingFunction
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.scale
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.transition
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.resources.WebDesignSystem
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.themed
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val ADD_BUTTON_TINT_ALPHA = 0.12f
private const val QUANTITY_MORPH_MS = 240
private const val QUANTITY_PRESSED_SCALE = 0.88
private const val QUANTITY_BUTTON_REM = 2.75
private const val COUNT_MIN_WIDTH = "1.75rem"

private val MorphTiming = TransitionTimingFunction.cubicBezier(0.2, 0.8, 0.2, 1.0)

val QuantityControlStyle = CssStyle {
    base {
        Modifier
            .flexShrink(0)
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.FlexEnd)
            .height(QUANTITY_BUTTON_REM.cssRem)
            .overflow(Overflow.Hidden)
            .borderRadius(QUANTITY_BUTTON_REM.cssRem)
            .fontSize(1.cssRem)
    }
    cssRule(MotionAllowed) {
        Modifier.transition(
            Transition.group(
                listOf("width", "background-color"),
                QUANTITY_MORPH_MS.ms,
                MorphTiming,
            ),
        )
    }
}

val QuantityRevealStyle = CssStyle {
    base { Modifier.flexShrink(0) }
    cssRule(MotionAllowed) {
        Modifier.transition(Transition.of("opacity", QUANTITY_MORPH_MS.ms, MorphTiming))
    }
}

val QuantityIconStyle = CssStyle {
    cssRule(MotionAllowed) {
        Modifier.transition(Transition.of("background-color", QUANTITY_MORPH_MS.ms, MorphTiming))
    }
}

val QuantityButtonStyle = CssStyle {
    base {
        Modifier
            .size(QUANTITY_BUTTON_REM.cssRem)
            .padding(0.px)
            .display(DisplayStyle.Flex)
            .alignItems(AlignItems.Center)
            .justifyContent(JustifyContent.Center)
            .flexShrink(0)
            .border(0.px, LineStyle.None, Colors.Transparent)
            .borderRadius(50.percent)
            .backgroundColor(Colors.Transparent)
            .cursor(Cursor.Pointer)
    }
    cssRule(MotionAllowed) {
        Modifier.transition(Transition.of("transform", BUTTON_TRANSITION_MS.ms))
    }
    cssRule(":active") { Modifier.scale(QUANTITY_PRESSED_SCALE) }
}

private class ShownQuantity(
    var value: Int,
)

@Composable
fun QuantityControl(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isExpanded = quantity > 0
    val shown = remember { ShownQuantity(1) }
    if (isExpanded) shown.value = quantity
    Row(
        QuantityControlStyle
            .toModifier()
            .backgroundColor(controlBackground(isExpanded))
            .styleModifier { property("width", controlWidth(isExpanded, shown.value)) }
            .then(modifier),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QuantityButton(
            WebDesignSystem.images.material_remove,
            DesignSystem.strings.quantity_decrease.desc(),
            ColorToken.OnAction,
            onDecrease,
            revealed(isExpanded),
        )
        QuantityCount(shown.value, revealed(isExpanded))
        QuantityButton(
            WebDesignSystem.images.material_add,
            increaseDescription(isExpanded),
            if (isExpanded) ColorToken.OnAction else ColorToken.ActionText,
            onIncrease,
        )
    }
}

@Composable
private fun QuantityCount(quantity: Int, modifier: Modifier) {
    Span(
        BodyMediumTextStyle
            .toModifier()
            .then(modifier)
            .textAlign(TextAlign.Center)
            .fontWeight(FontWeight.SemiBold)
            .styleModifier { property("width", countWidth(quantity)) }
            .attrsModifier { attr("aria-live", "polite") }
            .toAttrs(),
    ) {
        Text(quantity.toString())
    }
}

@Composable
private fun revealed(isExpanded: Boolean): Modifier {
    return QuantityRevealStyle
        .toModifier()
        .opacity(if (isExpanded) 1 else 0)
        .then(if (isExpanded) Modifier else Modifier.attrsModifier { attr("inert", "") })
}

@Composable
private fun controlBackground(isExpanded: Boolean): Color {
    if (isExpanded) return ColorToken.ButtonActive.themed
    return ColorToken.ActionText.themed
        .toRgb()
        .copyf(alpha = ADD_BUTTON_TINT_ALPHA)
}

private fun increaseDescription(isExpanded: Boolean): StringDesc {
    if (isExpanded) return DesignSystem.strings.quantity_increase.desc()
    return DesignSystem.strings.action_add.desc()
}

private fun countWidth(quantity: Int): String {
    return "max($COUNT_MIN_WIDTH, ${quantity.toString().length}ch)"
}

private fun controlWidth(isExpanded: Boolean, quantity: Int): String {
    if (!isExpanded) return "${QUANTITY_BUTTON_REM}rem"
    return "calc(${QUANTITY_BUTTON_REM * 2}rem + ${countWidth(quantity)})"
}

@Composable
private fun QuantityButton(
    icon: ImageResource,
    description: StringDesc,
    tint: ColorToken,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = description.localized()
    Button(
        QuantityButtonStyle
            .toModifier()
            .then(modifier)
            .toAttrs {
                attr("type", "button")
                attr("aria-label", label)
                onClick { onClick() }
            },
    ) {
        TintedIcon(
            icon,
            tint,
            QuantityIconStyle
                .toModifier()
                .size(1.25.cssRem),
        )
    }
}
