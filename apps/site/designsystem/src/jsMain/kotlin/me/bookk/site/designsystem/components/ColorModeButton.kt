package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.silk.components.icons.MoonIcon
import com.varabyte.kobweb.silk.components.icons.SunIcon
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.icerock.moko.resources.desc.StringDesc

@Composable
fun ColorModeButton(description: StringDesc) {
    var colorMode by ColorMode.currentState
    IconButton(
        onClick = { colorMode = colorMode.opposite },
        description = description,
    ) {
        if (colorMode.isLight) MoonIcon() else SunIcon()
    }
}
