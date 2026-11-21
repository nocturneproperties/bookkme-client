package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PickerPresentation
import me.bookk.designsystem.uistate.quantityOf
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val OptionsQuantityCardStyle = CssStyle.base {
    Modifier
        .padding(0.px)
        .gap(0.px)
}

val OptionsQuantityRowStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .minHeight(4.cssRem)
        .gap(0.75.cssRem)
        .padding(top = 0.5.cssRem, right = 0.75.cssRem, bottom = 0.5.cssRem, left = 1.cssRem)
}

@Composable
fun <T : PickerPresentation> OptionsQuantityList(
    state: OptionsMultiPickerState<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    if (!state.isVisible) return
    Column(modifier.gap(0.5.cssRem)) {
        Header(state.pickerTitle)
        AppCard(OptionsQuantityCardStyle.toModifier()) {
            OptionsQuantityPlaceholder(state)
            state.options.forEachIndexed { index, option ->
                if (index > 0) HorizontalDivider()
                OptionsQuantityRow(state, option, itemContent)
            }
        }
    }
}

@Composable
private fun <T : PickerPresentation> OptionsQuantityPlaceholder(state: OptionsMultiPickerState<T>) {
    val placeholder = state.placeholder ?: return
    if (state.options.isNotEmpty()) return
    TextBlock(
        placeholder,
        BodyMediumTextStyle
            .toModifier()
            .secondary()
            .padding(1.cssRem),
    )
}

@Composable
private fun <T : PickerPresentation> OptionsQuantityRow(
    state: OptionsMultiPickerState<T>,
    option: T,
    itemContent: @Composable (T) -> Unit,
) {
    Row(
        OptionsQuantityRowStyle.toModifier(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .flexGrow(1)
                .minWidth(0.px),
        ) {
            itemContent(option)
        }
        if (state.isEditable && state.isQuantityEditable) {
            OptionQuantityControl(state, option)
        }
    }
}

@Composable
private fun <T : PickerPresentation> OptionQuantityControl(
    state: OptionsMultiPickerState<T>,
    option: T,
) {
    val quantity = state.quantityOf(option)
    if (quantity == 0) {
        QuantityAddButton(onClick = { state.onQuantityIncreaseRequested(option) })
    } else {
        QuantityStepper(
            quantity = quantity,
            onIncrease = { state.onQuantityIncreaseRequested(option) },
            onDecrease = { state.onQuantityDecreaseRequested(option) },
        )
    }
}
