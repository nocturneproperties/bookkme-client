package me.bookk.site.designsystem.theme

import com.varabyte.kobweb.compose.css.ScrollBehavior
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fontFamily
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.modifiers.scrollBehavior
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.init.registerStyleBase
import com.varabyte.kobweb.silk.theme.colors.palette.background
import com.varabyte.kobweb.silk.theme.colors.palette.color
import org.jetbrains.compose.web.css.CSSMediaQuery
import org.jetbrains.compose.web.css.StylePropertyValue
import org.jetbrains.compose.web.css.px

@InitSilk
fun initDesignSystemTheme(ctx: InitSilkContext) {
    ctx.theme.palettes.light.background = WebColors.light.background
    ctx.theme.palettes.light.color = WebColors.light.primaryText
    ctx.theme.palettes.dark.background = WebColors.dark.background
    ctx.theme.palettes.dark.color = WebColors.dark.primaryText
    ctx.stylesheet.registerStyle("html") {
        cssRule(
            CSSMediaQuery.MediaFeature(
                "prefers-reduced-motion",
                StylePropertyValue("no-preference"),
            ),
        ) {
            Modifier.scrollBehavior(ScrollBehavior.Smooth)
        }
    }
    ctx.stylesheet.registerStyleBase("body") {
        Modifier
            .fontFamily(
                "-apple-system",
                "BlinkMacSystemFont",
                "Segoe UI",
                "Roboto",
                "Helvetica Neue",
                "sans-serif",
            )
            .fontSize(17.px)
            .lineHeight(1.5)
    }
}
