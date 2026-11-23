package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.animation
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.onAnimationEnd
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.translateY
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.animation.toAnimation
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.px

private const val ITEM_APPEAR_MS = 260

val ItemAppearKeyframes = Keyframes {
    from {
        Modifier
            .opacity(0)
            .translateY(0.5.cssRem)
    }
    to {
        Modifier
            .opacity(1)
            .translateY(0.px)
    }
}

val AppearingItemStyle = CssStyle.base {
    Modifier.fillMaxWidth()
}

val ItemAppearStyle = CssStyle {
    cssRule(MotionAllowed) {
        Modifier.animation(
            ItemAppearKeyframes.toAnimation(
                duration = ITEM_APPEAR_MS.ms,
                timingFunction = DecelerateTiming,
            ),
        )
    }
}

class ItemAppearance internal constructor() {
    internal var animatesNewItems = false
}

@Composable
fun rememberItemAppearance(isShown: Boolean = true): ItemAppearance {
    val appearance = remember { ItemAppearance() }
    SideEffect {
        if (isShown) appearance.animatesNewItems = true
    }
    return appearance
}

@Composable
fun AppearingItem(
    appearance: ItemAppearance,
    key: Any,
    content: @Composable ColumnScope.() -> Unit,
) {
    key(key) {
        var isAppearing by remember { mutableStateOf(appearance.animatesNewItems) }
        Column(
            AppearingItemStyle
                .toModifier()
                .then(if (isAppearing) ItemAppearStyle.toModifier() else Modifier)
                .onAnimationEnd { if (it.isOwnAnimation()) isAppearing = false },
            content = content,
        )
    }
}
