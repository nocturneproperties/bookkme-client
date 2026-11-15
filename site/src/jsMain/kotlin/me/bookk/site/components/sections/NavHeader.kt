package me.bookk.site.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.components.icons.MoonIcon
import com.varabyte.kobweb.silk.components.icons.SunIcon
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.components.text.SpanText
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.components.widgets.Logo
import me.bookk.site.designsystem.components.IconButton
import me.bookk.site.designsystem.components.LanguagePicker
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.cssRem

val NavHeaderStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(64.cssRem)
        .padding(topBottom = 1.cssRem, leftRight = 1.cssRem)
        .gap(0.75.cssRem)
}

@Composable
fun NavHeader() {
    Row(NavHeaderStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
        Link("/", variant = UndecoratedLinkVariant.then(UncoloredLinkVariant)) {
            Row(Modifier.gap(0.75.cssRem), verticalAlignment = Alignment.CenterVertically) {
                Logo(Modifier.height(1.75.cssRem))
                SpanText(
                    SiteRes.strings.site_app_name.desc().localized(),
                    Modifier
                        .fontSize(1.25.cssRem)
                        .fontWeight(FontWeight.Bold),
                )
            }
        }
        Spacer()
        LanguagePicker(SiteRes.strings.site_language.desc())
        ColorModeButton()
    }
}

@Composable
private fun ColorModeButton() {
    var colorMode by ColorMode.currentState
    IconButton(
        onClick = { colorMode = colorMode.opposite },
        description = SiteRes.strings.site_toggle_color_mode.desc(),
    ) {
        if (colorMode.isLight) MoonIcon() else SunIcon()
    }
}
