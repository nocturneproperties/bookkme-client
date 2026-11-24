package library.telegram

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import me.bookk.core.presentation.navigation.LocalIsCurrentScreen
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.resources.localized
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.uistate.ButtonState

private const val MAX_TEXT_LENGTH = 64

external interface TelegramBottomButton {
    fun setParams(params: TelegramBottomButtonParams): TelegramBottomButton

    fun hide(): TelegramBottomButton

    fun showProgress(leaveActive: Boolean): TelegramBottomButton

    fun hideProgress(): TelegramBottomButton

    fun onClick(callback: () -> Unit): TelegramBottomButton

    fun offClick(callback: () -> Unit): TelegramBottomButton
}

external interface TelegramBottomButtonParams {
    var text: String?
    var color: String?

    @JsName("text_color")
    var textColor: String?

    @JsName("is_active")
    var isActive: Boolean?

    @JsName("is_visible")
    var isVisible: Boolean?
}

internal class BottomButtonColors(
    val active: String,
    val onActive: String,
    val inactive: String,
    val onInactive: String,
)

internal data class BottomButtonSpec(
    val text: String,
    val isVisible: Boolean,
    val isActive: Boolean,
    val isProgressVisible: Boolean,
    val color: String,
    val textColor: String,
)

@Composable
fun TelegramMainButton(webApp: TelegramWebApp, state: ButtonState) {
    BottomButton(webApp.mainButton, state)
}

@Composable
fun TelegramSecondaryButton(webApp: TelegramWebApp, state: ButtonState) {
    if (webApp.supports(TelegramFeature.SECONDARY_BUTTON)) {
        BottomButton(webApp.secondaryButton, state)
    } else {
        TextButton(state)
    }
}

@Composable
private fun BottomButton(button: TelegramBottomButton, state: ButtonState) {
    if (!LocalIsCurrentScreen.current) return
    val spec = state.bottomButtonSpec(state.text.localized(), bottomButtonColors())
    val onClick by rememberUpdatedState(state.onClick)
    DisposableEffect(button) {
        val onPressed = { onClick?.invoke() ?: Unit }
        button.onClick(onPressed)
        onDispose {
            button.offClick(onPressed)
            button.hide()
        }
    }
    LaunchedEffect(button, spec) {
        button.render(spec)
    }
}

@Composable
private fun bottomButtonColors(): BottomButtonColors {
    return BottomButtonColors(
        active = ColorToken.ButtonActive.themed.toTelegramColor(),
        onActive = ColorToken.OnAction.themed.toTelegramColor(),
        inactive = ColorToken.ButtonInactive.themed.toTelegramColor(),
        onInactive = ColorToken.ActionTextDisabled.themed.toTelegramColor(),
    )
}

internal fun ButtonState.bottomButtonSpec(
    text: String,
    colors: BottomButtonColors,
): BottomButtonSpec {
    val label = text.limitTo(MAX_TEXT_LENGTH)
    val isActive = isEnabled && !isLoading
    return BottomButtonSpec(
        text = label,
        isVisible = isVisible && label.isNotEmpty(),
        isActive = isActive,
        isProgressVisible = isLoading,
        color = if (isActive) colors.active else colors.inactive,
        textColor = if (isActive) colors.onActive else colors.onInactive,
    )
}

internal fun TelegramBottomButton.render(spec: BottomButtonSpec) {
    if (!spec.isVisible) {
        hide()
        return
    }
    if (!spec.isProgressVisible) hideProgress()
    setParams(spec.toParams())
    if (spec.isProgressVisible) showProgress(leaveActive = false)
}

private fun BottomButtonSpec.toParams(): TelegramBottomButtonParams {
    return js("{}").unsafeCast<TelegramBottomButtonParams>().also {
        it.text = text
        it.color = color
        it.textColor = textColor
        it.isActive = isActive
        it.isVisible = true
    }
}
