package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent

private const val SUCCESS_MARK_TINT_ALPHA = 0.16f

val SuccessMarkStyle = CssStyle.base {
    Modifier
        .size(5.cssRem)
        .flexShrink(0)
        .borderRadius(50.percent)
        .backgroundColor(
            ColorToken.Success
                .resolve(colorMode.webColors())
                .toRgb()
                .copyf(alpha = SUCCESS_MARK_TINT_ALPHA),
        )
}

@Composable
fun SuccessMark(modifier: Modifier = Modifier) {
    Box(
        SuccessMarkStyle
            .toModifier()
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        TintedIcon(
            DesignSystem.images.material_check,
            ColorToken.Success,
            Modifier.size(2.5.cssRem),
        )
    }
}
