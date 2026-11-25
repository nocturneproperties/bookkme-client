package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

val DayDateStyle = CssStyle.base {
    Modifier
        .flexShrink(0)
        .minWidth(3.cssRem)
}

@Composable
fun DayDate(weekday: String, day: String, modifier: Modifier = Modifier) {
    Column(
        DayDateStyle
            .toModifier()
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Span(
            Modifier
                .fontSize(1.75.cssRem)
                .lineHeight(2.cssRem)
                .fontWeight(FontWeight.Bold)
                .toAttrs(),
        ) { Text(day) }
        Span(
            Modifier
                .fontSize(0.8125.cssRem)
                .lineHeight(1.125.cssRem)
                .secondary()
                .toAttrs(),
        ) { Text(weekday) }
    }
}
