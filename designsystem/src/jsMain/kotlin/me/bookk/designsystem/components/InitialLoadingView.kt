package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import org.jetbrains.compose.web.css.cssRem

@Composable
fun InitialLoadingView(modifier: Modifier = Modifier) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(topBottom = 1.5.cssRem)
            .then(modifier),
        contentAlignment = Alignment.Center,
    ) {
        Spinner(Modifier.size(1.5.cssRem))
    }
}
