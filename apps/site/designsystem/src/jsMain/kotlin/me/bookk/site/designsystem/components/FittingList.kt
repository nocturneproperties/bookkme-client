package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.attrsModifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import org.jetbrains.compose.web.css.px
import org.w3c.dom.Element
import org.w3c.dom.asList

private const val FITTING_ITEM_ATTRIBUTE = "data-fitting-item"
private const val FIT_TOLERANCE_PX = 0.5

val FittingListStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .minHeight(0.px)
        .overflow(Overflow.Hidden)
}

fun Modifier.fittingItem(): Modifier {
    return attrsModifier { attr(FITTING_ITEM_ATTRIBUTE, "") }
}

@Composable
fun <T> FittingList(
    items: List<T>,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.(visibleItems: List<T>) -> Unit,
) {
    var area by remember { mutableStateOf<Element?>(null) }
    var visibleCount by remember(items) { mutableIntStateOf(items.size) }
    var resizeCount by remember { mutableIntStateOf(0) }
    ObserveWindowResize {
        visibleCount = items.size
        resizeCount++
    }
    Column(
        FittingListStyle
            .toModifier()
            .then(modifier)
            .attrsModifier {
                ref { element ->
                    area = element
                    onDispose { area = null }
                }
            },
    ) {
        content(items.take(visibleCount))
    }
    LaunchedEffect(area, items, visibleCount, resizeCount) {
        val fittingCount = area?.fittingItemCount() ?: return@LaunchedEffect
        if (fittingCount < visibleCount) visibleCount = fittingCount
    }
}

private fun Element.fittingItemCount(): Int {
    val bottom = getBoundingClientRect().bottom + FIT_TOLERANCE_PX
    return querySelectorAll("[$FITTING_ITEM_ATTRIBUTE]")
        .asList()
        .takeWhile { it.unsafeCast<Element>().getBoundingClientRect().bottom <= bottom }
        .size
}
