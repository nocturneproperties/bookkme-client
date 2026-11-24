package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.BodyMediumTextStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Ol
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul

enum class TextListType {
    BULLET,
    ORDERED,
}

val TextListStyle = CssStyle.base {
    Modifier
        .margin(0.px)
        .padding(left = 1.5.cssRem)
}

val TextListItemStyle = CssStyle.base {
    Modifier
        .margin(bottom = 0.5.cssRem)
}

@Composable
fun TextList(
    items: List<StringDesc>,
    modifier: Modifier = Modifier,
    type: TextListType = TextListType.BULLET,
) {
    TextList(items, modifier, type) { Text(it.localized()) }
}

@Composable
fun <T> TextList(
    items: List<T>,
    modifier: Modifier = Modifier,
    type: TextListType = TextListType.BULLET,
    itemContent: @Composable (T) -> Unit,
) {
    val listModifier = BodyMediumTextStyle
        .toModifier()
        .then(TextListStyle.toModifier())
        .then(modifier)
    when (type) {
        TextListType.BULLET -> Ul(listModifier.toAttrs()) { TextListItems(items, itemContent) }
        TextListType.ORDERED -> Ol(listModifier.toAttrs()) { TextListItems(items, itemContent) }
    }
}

@Composable
private fun <T> TextListItems(items: List<T>, itemContent: @Composable (T) -> Unit) {
    items.forEach { item ->
        Li(TextListItemStyle.toModifier().toAttrs()) { itemContent(item) }
    }
}
