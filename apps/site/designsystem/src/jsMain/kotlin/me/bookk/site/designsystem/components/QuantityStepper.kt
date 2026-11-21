package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
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
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

private const val ADD_BUTTON_TINT_ALPHA = 0.12f

val QuantityStepperStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .flexShrink(0)
        .borderRadius(2.75.cssRem)
        .backgroundColor(colors.buttonActive)
        .color(colors.onAction)
}

val QuantityButtonStyle = CssStyle.base {
    Modifier
        .size(2.75.cssRem)
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

val QuantityAddButtonStyle = CssStyle.base {
    Modifier.backgroundColor(
        ColorToken.ActionText
            .resolve(colorMode.webColors())
            .toRgb()
            .copyf(alpha = ADD_BUTTON_TINT_ALPHA),
    )
}

@Composable
fun QuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        QuantityStepperStyle
            .toModifier()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QuantityButton(
            WebDesignSystem.images.material_remove,
            DesignSystem.strings.quantity_decrease.desc(),
            ColorToken.OnAction,
            onDecrease,
        )
        Span(
            BodyMediumTextStyle
                .toModifier()
                .minWidth(1.cssRem)
                .textAlign(TextAlign.Center)
                .fontWeight(FontWeight.SemiBold)
                .attrsModifier { attr("aria-live", "polite") }
                .toAttrs(),
        ) {
            Text(quantity.toString())
        }
        QuantityButton(
            WebDesignSystem.images.material_add,
            DesignSystem.strings.quantity_increase.desc(),
            ColorToken.OnAction,
            onIncrease,
        )
    }
}

@Composable
fun QuantityAddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    QuantityButton(
        WebDesignSystem.images.material_add,
        DesignSystem.strings.action_add.desc(),
        ColorToken.ActionText,
        onClick,
        QuantityAddButtonStyle
            .toModifier()
            .then(modifier),
    )
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
        TintedIcon(icon, tint, Modifier.size(1.25.cssRem))
    }
}
