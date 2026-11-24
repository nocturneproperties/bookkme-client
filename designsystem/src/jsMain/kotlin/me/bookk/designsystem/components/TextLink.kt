package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.LinkStyle
import com.varabyte.kobweb.silk.style.addVariant
import com.varabyte.kobweb.silk.style.selectors.hover
import com.varabyte.kobweb.silk.style.selectors.link
import com.varabyte.kobweb.silk.style.selectors.visited
import com.varabyte.kobweb.silk.style.vars.color.ColorVar
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors

val HighlightedLinkVariant = LinkStyle.addVariant {
    val restingColor = Modifier.color(ColorVar.value())
    link { restingColor }
    visited { restingColor }
    hover {
        Modifier
            .color(ColorToken.ActionText.resolve(colorMode.webColors()))
            .textDecorationLine(TextDecorationLine.None)
    }
}

@Composable
fun TextLink(text: StringDesc, path: String, modifier: Modifier = Modifier) {
    Link(path, text.localized(), modifier, variant = HighlightedLinkVariant)
}
