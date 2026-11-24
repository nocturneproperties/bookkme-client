package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.Cursor
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.graphics.Colors
import com.varabyte.kobweb.compose.ui.modifiers.border
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.cursor
import com.varabyte.kobweb.compose.ui.modifiers.fontSize
import com.varabyte.kobweb.compose.ui.modifiers.left
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.opacity
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.toModifier
import kotlinx.browser.window
import kotlinx.datetime.LocalDate
import me.bookk.designsystem.uistate.DatePickerState
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.max
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Input
import org.w3c.dom.HTMLInputElement

val DatePickerOverlayStyle = CssStyle {
    base {
        Modifier
            .position(Position.Absolute)
            .top(0.px)
            .left(0.px)
            .size(100.percent)
            .margin(0.px)
            .padding(0.px)
            .border(0.px, LineStyle.None, Colors.Transparent)
            .boxSizing(BoxSizing.BorderBox)
            .fontSize(1.cssRem)
            .opacity(0)
            .cursor(Cursor.Pointer)
    }
    cssRule("::-webkit-calendar-picker-indicator") {
        Modifier
            .position(Position.Absolute)
            .top(0.px)
            .left(0.px)
            .size(100.percent)
            .margin(0.px)
            .padding(0.px)
            .cursor(Cursor.Pointer)
    }
}

@Composable
fun NativeDatePicker(state: DatePickerState, modifier: Modifier = Modifier) {
    var input by remember { mutableStateOf<HTMLInputElement?>(null) }
    val commit = remember { DatePickerCommit(browserDateCommitMode()) }
    Input(
        InputType.Date,
        DatePickerOverlayStyle
            .toModifier()
            .then(modifier)
            .toAttrs {
                tabIndex(-1)
                attr("aria-hidden", "true")
                state.minDate?.let { min(it.toString()) }
                state.maxDate?.let { max(it.toString()) }
                value(state.pickedDate?.toString().orEmpty())
                onChange { event ->
                    commit.change(event.value.toLocalDateOrNull())?.let {
                        state.onDatePicked?.invoke(it)
                    }
                }
                onBlur {
                    commit.close()?.let { state.onDatePicked?.invoke(it) }
                }
                ref { element ->
                    input = element
                    onDispose { input = null }
                }
            },
    )
    LaunchedEffect(state.isDatePickerVisible) {
        if (!state.isDatePickerVisible) return@LaunchedEffect
        input?.openPicker()
        state.isDatePickerVisible = false
    }
}

private fun String.toLocalDateOrNull(): LocalDate? {
    return takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
}

private fun browserDateCommitMode(): DateCommitMode {
    return DateCommitMode.of(window.navigator.userAgent, window.navigator.maxTouchPoints)
}

private fun HTMLInputElement.openPicker() {
    val element = asDynamic()
    val isShown = element.showPicker != undefined &&
        runCatching { element.showPicker() }.isSuccess
    if (isShown) return
    focus()
    click()
}
