package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem

val AppCardStyle = CssStyle.base {
    Modifier
        .backgroundColor(colorMode.webColors().elevated)
        .borderRadius(1.cssRem)
        .padding(1.5.cssRem)
        .gap(0.5.cssRem)
}

@Composable
fun AppCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        AppCardStyle
            .toModifier()
            .then(modifier),
    ) {
        content()
    }
}
