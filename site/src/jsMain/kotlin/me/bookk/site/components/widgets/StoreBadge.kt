package me.bookk.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem

val StoreBadgeStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .backgroundColor(ColorToken.ButtonActive.resolve(colors))
        .color(ColorToken.OnAction.resolve(colors))
        .fontWeight(FontWeight.SemiBold)
        .borderRadius(0.75.cssRem)
        .padding(topBottom = 0.75.cssRem, leftRight = 1.5.cssRem)
}

@Composable
fun StoreBadge(store: StringDesc) {
    Box(StoreBadgeStyle.toModifier()) {
        SpanText(store.localized())
    }
}
