package me.bookk.designsystem

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextDecoration
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.core.LogFactory
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.ValidationState

private val logger = LogFactory.createLogger("DesignSystemExtensions")

@Composable
fun ColorResource.get(): Color {
    // If recreate context is off
    LocalConfiguration.current
    val context: Context = LocalContext.current
    return Color(getColor(context))
}

@Composable
fun ImageResource.painter(): Painter {
    return painterResource(this)
}

@Composable
fun StringDesc.html(): AnnotatedString {
    val uriHandler = if (!LocalInspectionMode.current) {
        LocalUriHandler.current
    } else null
    return AnnotatedString.fromHtml(
        htmlString = localized(),
        linkStyles = TextLinkStyles(
            style = SpanStyle(
                color = LocalColors.current.actionText,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline
            )
        ),
        linkInteractionListener = { annotation ->
            when (annotation) {
                is LinkAnnotation.Url -> {
                    runCatching {
                        uriHandler?.openUri(annotation.url)
                    }.getOrElse {
                        logger.e("Failed to open url: ${annotation.url}", it)
                    }
                }
            }
        }
    )
}

val ValidationState.color: Color
    @Composable
    get() = when (this) {
        ValidationState.ERROR -> LocalColors.current.error
        ValidationState.WARNING -> LocalColors.current.error
        ValidationState.DEFAULT -> LocalColors.current.secondaryText
    }