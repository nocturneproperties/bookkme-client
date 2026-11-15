package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.silk.components.forms.Button
import com.varabyte.kobweb.silk.components.forms.ButtonStyle
import com.varabyte.kobweb.silk.components.forms.ButtonVars
import com.varabyte.kobweb.silk.style.addVariantBase
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.site.designsystem.resources.localized
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

val CircleButtonVariant = ButtonStyle.addVariantBase {
    Modifier
        .padding(0.px)
        .borderRadius(50.percent)
        .setVariable(ButtonVars.BackgroundDefaultColor, Colors.Transparent)
}

@Composable
fun IconButton(
    onClick: () -> Unit,
    description: StringDesc? = null,
    content: @Composable () -> Unit,
) {
    val label = description?.localized()
    Button(
        onClick = { onClick() },
        modifier = Modifier
            .setVariable(ButtonVars.FontSize, 1.em)
            .attrsModifier { label?.let { attr("aria-label", it) } },
        variant = CircleButtonVariant,
    ) {
        content()
    }
}
