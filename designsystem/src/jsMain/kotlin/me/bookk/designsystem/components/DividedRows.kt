package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.JustifyContent
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.RowScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.borderBottom
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val DividedRowsStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .borderTop(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
}

val DividedRowStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .gap(1.25.cssRem)
        .padding(topBottom = 1.75.cssRem)
        .flexWrap(FlexWrap.Wrap)
        .justifyContent(JustifyContent.SpaceBetween)
        .borderBottom(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
}

@Composable
fun DividedRows(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        DividedRowsStyle
            .toModifier()
            .then(modifier),
    ) {
        content()
    }
}

@Composable
fun DividedRow(
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        DividedRowStyle
            .toModifier()
            .then(modifier),
        verticalAlignment = verticalAlignment,
    ) {
        content()
    }
}
