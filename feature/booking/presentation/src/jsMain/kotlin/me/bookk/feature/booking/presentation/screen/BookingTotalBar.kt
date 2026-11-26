package me.bookk.feature.booking.presentation.screen

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.components.RevealingBottomBar
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val TotalContentModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(topBottom = 0.75.cssRem, leftRight = 1.25.cssRem)
    .gap(1.cssRem)

@Composable
internal fun BookingTotalBar(total: BookingTotal?) {
    RevealingBottomBar(total) { TotalContent(it) }
}

@Composable
private fun TotalContent(total: BookingTotal) {
    Row(
        TotalContentModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextBlock(
            total.summary,
            BodyLargeTextStyle
                .toModifier()
                .minWidth(0.px)
                .margin(0.px)
                .secondary(),
        )
        TextBlock(
            total.price,
            HeadlineSmallTextStyle
                .toModifier()
                .fontWeight(FontWeight.Bold)
                .flexShrink(0)
                .margin(0.px),
        )
    }
}
