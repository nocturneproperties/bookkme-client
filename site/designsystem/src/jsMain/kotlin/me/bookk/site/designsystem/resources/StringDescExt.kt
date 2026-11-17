package me.bookk.site.designsystem.resources

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.provider.RemoteJsStringLoader

@Composable
fun StringDesc.localized(): String {
    WebLocalization.language
    return WebLocalization.resolve(this)
}

@Composable
fun LocalizedContent(appStrings: RemoteJsStringLoader, content: @Composable () -> Unit) {
    LaunchedEffect(appStrings) {
        WebLocalization.load(appStrings)
    }
    if (WebLocalization.provider != null) content()
}
