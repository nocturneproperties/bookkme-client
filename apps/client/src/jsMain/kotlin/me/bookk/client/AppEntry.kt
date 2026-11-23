package me.bookk.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.core.App
import com.varabyte.kobweb.silk.SilkApp
import com.varabyte.kobweb.silk.components.layout.Surface
import com.varabyte.kobweb.silk.init.InitSilk
import com.varabyte.kobweb.silk.init.InitSilkContext
import com.varabyte.kobweb.silk.style.common.SmoothColorStyle
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import com.varabyte.kobweb.silk.theme.colors.loadFromLocalStorage
import com.varabyte.kobweb.silk.theme.colors.saveToLocalStorage
import com.varabyte.kobweb.silk.theme.colors.systemPreference
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.android.feature.dashboard.resources.DashboardRes
import me.bookk.client.resources.ClientRes
import me.bookk.site.designsystem.resources.LocalizedContent
import org.jetbrains.compose.web.css.vh

private const val COLOR_MODE_KEY = "bookkme:colorMode"

@InitSilk
fun initColorMode(ctx: InitSilkContext) {
    ctx.config.initialColorMode =
        ColorMode.loadFromLocalStorage(COLOR_MODE_KEY) ?: ColorMode.systemPreference
}

@App
@Composable
fun AppEntry(content: @Composable () -> Unit) {
    SilkApp {
        val colorMode = ColorMode.current
        LaunchedEffect(colorMode) {
            colorMode.saveToLocalStorage(COLOR_MODE_KEY)
        }
        Surface(
            SmoothColorStyle
                .toModifier()
                .minHeight(100.vh),
        ) {
            LocalizedContent(
                ClientRes.strings.stringsLoader +
                    AuthRes.strings.stringsLoader +
                    DashboardRes.strings.stringsLoader +
                    BookingRes.stringsLoader,
            ) {
                content()
            }
        }
    }
}
