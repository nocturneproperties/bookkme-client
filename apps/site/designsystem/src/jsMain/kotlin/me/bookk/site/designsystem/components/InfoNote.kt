package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.resources.WebDesignSystem
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.primary
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private const val INFO_NOTE_TINT_ALPHA = 0.2f

val InfoNoteStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .boxSizing(BoxSizing.BorderBox)
        .gap(0.75.cssRem)
        .padding(1.cssRem)
        .borderRadius(1.cssRem)
        .backgroundColor(
            ColorToken.ActionText
                .resolve(colorMode.webColors())
                .toRgb()
                .copyf(alpha = INFO_NOTE_TINT_ALPHA),
        )
}

@Composable
fun InfoNote(text: StringDesc, modifier: Modifier = Modifier, title: StringDesc? = null) {
    Row(
        InfoNoteStyle
            .toModifier()
            .then(modifier),
    ) {
        TintedIcon(WebDesignSystem.images.material_info, ColorToken.ActionText)
        Column(Modifier.minWidth(0.px).gap(0.125.cssRem)) {
            title?.let {
                TextBlock(
                    it,
                    BodyMediumTextStyle
                        .toModifier()
                        .fontWeight(FontWeight.SemiBold)
                        .margin(0.px)
                        .primary(),
                )
            }
            TextBlock(
                text,
                BodyMediumTextStyle
                    .toModifier()
                    .margin(0.px)
                    .primary(),
            )
        }
    }
}
