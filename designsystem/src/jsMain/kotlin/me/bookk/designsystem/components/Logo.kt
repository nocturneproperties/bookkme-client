package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.silk.components.graphics.Image
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.localized
import org.jetbrains.compose.web.css.DisplayStyle

@Composable
fun Logo(description: StringDesc, modifier: Modifier = Modifier) {
    Image(
        src = if (ColorMode.current.isLight) "/logo-light.svg" else "/logo-dark.svg",
        description = description.localized(),
        modifier = modifier.display(DisplayStyle.Block),
    )
}
