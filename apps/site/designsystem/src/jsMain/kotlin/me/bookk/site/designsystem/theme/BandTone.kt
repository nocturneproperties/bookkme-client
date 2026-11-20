package me.bookk.site.designsystem.theme

import com.varabyte.kobweb.compose.css.StyleVariable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.setVariable
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import me.bookk.designsystem.resources.color.ColorToken
import org.jetbrains.compose.web.css.CSSColorValue

enum class BandTone {
    PLAIN,
    ALTERNATE,
    DARK,
}

data class BandSurfaces(
    val background: ColorToken,
    val card: ColorToken,
    val inset: ColorToken,
)

val SurfaceCardColor by StyleVariable<CSSColorValue>(prefix = "bookk")
val SurfaceInsetColor by StyleVariable<CSSColorValue>(prefix = "bookk")

fun BandTone.surfaces(): BandSurfaces {
    return when (this) {
        BandTone.PLAIN, BandTone.DARK -> BandSurfaces(
            ColorToken.Background,
            ColorToken.Elevated,
            ColorToken.Background,
        )

        BandTone.ALTERNATE -> BandSurfaces(
            ColorToken.Elevated,
            ColorToken.Background,
            ColorToken.Elevated,
        )
    }
}

fun BandTone.colorMode(pageMode: ColorMode): ColorMode {
    return if (this == BandTone.DARK) ColorMode.DARK else pageMode
}

fun Modifier.surfaceColors(surfaces: BandSurfaces, colors: WebColors): Modifier {
    return this
        .setVariable(SurfaceCardColor, surfaces.card.resolve(colors))
        .setVariable(SurfaceInsetColor, surfaces.inset.resolve(colors))
}
