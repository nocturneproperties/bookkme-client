package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AnimationIterationCount
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.animation
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.translateX
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.style.animation.Keyframes
import com.varabyte.kobweb.silk.style.animation.toAnimation
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.theme.themed
import org.jetbrains.compose.web.css.AnimationTimingFunction
import org.jetbrains.compose.web.css.ms
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

val IndeterminateProgressKeyframes = Keyframes {
    from { Modifier.translateX((-100).percent) }
    to { Modifier.translateX(300.percent) }
}

@Composable
fun LinearProgress(modifier: Modifier = Modifier) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(4.px)
            .overflow(Overflow.Hidden)
            .backgroundColor(ColorToken.Divider.themed)
            .then(modifier),
    ) {
        Box(
            Modifier
                .width(33.percent)
                .fillMaxHeight()
                .backgroundColor(ColorToken.ActionText.themed)
                .animation(
                    IndeterminateProgressKeyframes.toAnimation(
                        duration = 1200.ms,
                        timingFunction = AnimationTimingFunction.EaseInOut,
                        iterationCount = AnimationIterationCount.Infinite,
                    ),
                ),
        )
    }
}
