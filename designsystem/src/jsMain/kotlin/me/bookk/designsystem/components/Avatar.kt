package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.ObjectFit
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.objectFit
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.toAttrs
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.dom.Img

@Composable
fun Avatar(
    initials: String,
    photoUrl: String?,
    modifier: Modifier = Modifier,
    size: InitialsAvatarSize = InitialsAvatarSize.REGULAR,
) {
    var hasPhotoFailed by remember(photoUrl) { mutableStateOf(false) }
    if (photoUrl == null || hasPhotoFailed) {
        InitialsAvatar(initials, modifier, size)
    } else {
        PhotoAvatar(photoUrl, modifier, size) { hasPhotoFailed = true }
    }
}

@Composable
private fun PhotoAvatar(
    photoUrl: String,
    modifier: Modifier,
    size: InitialsAvatarSize,
    onLoadFailed: () -> Unit,
) {
    Img(
        src = photoUrl,
        alt = "",
        attrs = Modifier
            .flexShrink(0)
            .size(size.diameterRem.cssRem)
            .borderRadius(50.percent)
            .objectFit(ObjectFit.Cover)
            .then(modifier)
            .toAttrs {
                attr("aria-hidden", "true")
                attr("referrerpolicy", "no-referrer")
                addEventListener("error") { onLoadFailed() }
            },
    )
}
