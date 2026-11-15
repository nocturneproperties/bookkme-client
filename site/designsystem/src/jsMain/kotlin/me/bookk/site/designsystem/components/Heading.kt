package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.toAttrs
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.site.designsystem.resources.localized
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.Text

enum class HeadingLevel {
    H1,
    H2,
    H3,
}

@Composable
fun Heading(text: StringDesc, level: HeadingLevel, modifier: Modifier = Modifier) {
    val content: @Composable () -> Unit = { Text(text.localized()) }
    when (level) {
        HeadingLevel.H1 -> H1(modifier.toAttrs()) { content() }
        HeadingLevel.H2 -> H2(modifier.toAttrs()) { content() }
        HeadingLevel.H3 -> H3(modifier.toAttrs()) { content() }
    }
}
