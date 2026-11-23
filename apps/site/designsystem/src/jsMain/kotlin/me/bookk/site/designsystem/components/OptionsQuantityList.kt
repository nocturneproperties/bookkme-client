package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PickerPresentation
import me.bookk.designsystem.uistate.quantityOf
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val OptionsQuantityRowStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .boxSizing(BoxSizing.BorderBox)
        .backgroundColor(colorMode.webColors().elevated)
        .borderRadius(1.cssRem)
        .minHeight(4.5.cssRem)
        .gap(0.75.cssRem)
        .padding(top = 0.75.cssRem, right = 0.75.cssRem, bottom = 0.75.cssRem, left = 1.cssRem)
}

@Composable
fun <T : PickerPresentation> OptionsQuantityList(
    state: OptionsMultiPickerState<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    OptionsQuantityList(state, state.pickerTitle, state.options, modifier, itemContent)
}

@Composable
fun <T : PickerPresentation> OptionsQuantityList(
    state: OptionsMultiPickerState<T>,
    title: StringDesc,
    options: List<T>,
    modifier: Modifier = Modifier,
    itemContent: @Composable (T) -> Unit,
) {
    if (!state.isVisible) return
    Column(
        Modifier
            .fillMaxWidth()
            .gap(0.5.cssRem)
            .then(modifier),
    ) {
        Header(title)
        OptionsQuantityPlaceholder(state.placeholder, options)
        val appearance = rememberItemAppearance()
        options.forEach { option ->
            AppearingItem(appearance, option.pickerItemId) {
                OptionsQuantityRow(state, option, itemContent)
            }
        }
    }
}

@Composable
private fun OptionsQuantityPlaceholder(placeholder: StringDesc?, options: List<*>) {
    if (placeholder == null || options.isNotEmpty()) return
    AppCard(
        Modifier
            .fillMaxWidth()
            .padding(1.cssRem),
    ) {
        TextBlock(
            placeholder,
            BodyMediumTextStyle
                .toModifier()
                .secondary(),
        )
    }
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
    QuantityControl(
        quantity = state.quantityOf(option),
        onIncrease = { state.onQuantityIncreaseRequested(option) },
        onDecrease = { state.onQuantityDecreaseRequested(option) },
    )
}
