package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.site.designsystem.resources.localized

@Composable
fun TextLink(text: StringDesc, path: String, modifier: Modifier = Modifier) {
    Link(path, text.localized(), modifier, variant = UncoloredLinkVariant)
}
