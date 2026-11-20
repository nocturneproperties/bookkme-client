package me.bookk.site.layout

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.DisplayStyle

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Image(
        src = if (ColorMode.current.isLight) "/logo-light.svg" else "/logo-dark.svg",
        description = SiteRes.strings.site_app_name.desc().localized(),
        modifier = modifier.display(DisplayStyle.Block),
    )
}
