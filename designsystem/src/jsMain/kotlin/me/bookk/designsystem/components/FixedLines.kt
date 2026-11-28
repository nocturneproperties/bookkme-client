package me.bookk.designsystem.components

import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.lineHeight
import com.varabyte.kobweb.compose.ui.styleModifier

fun Modifier.fixedLines(count: Int, lineHeight: Double): Modifier {
    return lineHeight(lineHeight).styleModifier {
        property("display", "-webkit-box")
        property("-webkit-box-orient", "vertical")
        property("-webkit-line-clamp", count)
        property("line-clamp", count)
        property("overflow", "hidden")
        property("min-height", "${count * lineHeight}em")
    }
}
