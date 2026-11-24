package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.AppLanguage
import me.bookk.designsystem.resources.WebLocalization
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.attributes.selected
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.Select
import org.jetbrains.compose.web.dom.Text

val LanguagePickerStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .backgroundColor(Colors.Transparent)
        .color(colors.primaryText)
        .border(1.px, LineStyle.Solid, colors.divider)
        .borderRadius(0.375.cssRem)
        .padding(topBottom = 0.125.cssRem, leftRight = 0.25.cssRem)
        .fontSize(0.75.cssRem)
        .fontWeight(FontWeight.SemiBold)
        .cursor(Cursor.Pointer)
}

@Composable
fun LanguagePicker(label: StringDesc, modifier: Modifier = Modifier) {
    val ctx = rememberPageContext()
    val current = WebLocalization.language
    val description = label.localized()
    Select(
        LanguagePickerStyle
            .toModifier()
            .then(modifier)
            .toAttrs {
                attr("aria-label", description)
                onChange { event ->
                    AppLanguage.entries.firstOrNull {
                        it.tag == event.value
                    }?.let { ctx.switchTo(it) }
                }
            },
    ) {
        AppLanguage.entries.forEach { language ->
            Option(
                language.tag,
                attrs = {
                    attr("lang", language.tag)
                    attr("aria-label", language.autonym)
                    if (language == current) selected()
                },
            ) {
                Text(language.abbreviation)
            }
        }
    }
}

private fun PageContext.switchTo(language: AppLanguage) {
    WebLocalization.select(language)
    router.navigateTo(language.localize(route.path))
}
