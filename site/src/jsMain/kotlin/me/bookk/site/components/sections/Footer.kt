package me.bookk.site.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.columnGap
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.rowGap
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.content.SiteNavigation
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.theme.LabelSmallTextStyle
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.themed
import me.bookk.site.designsystem.theme.webColors
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val FooterStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .backgroundColor(ColorToken.Elevated.resolve(colors))
        .borderTop(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colors))
        .color(ColorToken.SecondaryText.resolve(colors))
        .padding(topBottom = 1.25.cssRem, leftRight = 1.cssRem)
}

val FooterContentStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(64.cssRem)
        .gap(0.75.cssRem)
}

val FooterLegalStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .borderTop(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
        .padding(top = 0.75.cssRem)
        .rowGap(0.5.cssRem)
        .columnGap(2.cssRem)
        .flexWrap(FlexWrap.Wrap)
}

@Composable
fun Footer(modifier: Modifier = Modifier) {
    Box(
        FooterStyle
            .toModifier()
            .then(modifier),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(FooterContentStyle.toModifier()) {
            TextBlock(SiteRes.strings.site_footer.desc(), LabelSmallTextStyle.toModifier())
            Row(FooterLegalStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
                TextBlock(SiteNavigation.copyright(), LabelSmallTextStyle.toModifier())
                FooterLinks()
            }
        }
    }
}

@Composable
private fun FooterLinks() {
    Row(Modifier.gap(0.75.cssRem), verticalAlignment = Alignment.CenterVertically) {
        SiteNavigation.footer.forEachIndexed { index, link ->
            if (index > 0) LinkSeparator()
            TextLink(
                link.title,
                link.path,
                LabelSmallTextStyle
                    .toModifier()
                    .color(ColorToken.PrimaryText.themed),
            )
        }
    }
}

@Composable
private fun LinkSeparator() {
    Box(
        Modifier
            .width(1.px)
            .height(0.75.cssRem)
            .backgroundColor(ColorToken.Divider.themed),
    )
}
