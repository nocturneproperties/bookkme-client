package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.active
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

enum class HeadingLevel {
    H1,
    H2,
    H3,
}

@Composable
fun Heading(text: StringDesc, level: HeadingLevel, modifier: Modifier = Modifier) {
    HeadingElement(level, modifier) { Text(text.localized()) }
}

@Composable
fun AccentHeading(
    text: StringDesc,
    accent: StringDesc,
    level: HeadingLevel,
    modifier: Modifier = Modifier,
) {
    HeadingElement(level, modifier) {
        Text("${text.localized()} ")
        Span(Modifier.active().toAttrs()) { Text(accent.localized()) }
    }
}

@Composable
private fun HeadingElement(
    level: HeadingLevel,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    when (level) {
        HeadingLevel.H1 -> H1(modifier.toAttrs()) { content() }
        HeadingLevel.H2 -> H2(modifier.toAttrs()) { content() }
        HeadingLevel.H3 -> H3(modifier.toAttrs()) { content() }
    }
}
