package me.bookk.site.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.theme.BodySmallTextStyle
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val FooterStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .borderTop(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colors))
        .color(ColorToken.SecondaryText.resolve(colors))
        .textAlign(TextAlign.Center)
        .padding(topBottom = 2.cssRem, leftRight = 1.cssRem)
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Box(
        FooterStyle
            .toModifier()
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        TextBlock(SiteRes.strings.site_footer.desc(), BodySmallTextStyle.toModifier())
    }
}
