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

enum class TelegramButtonStyle {
    PRIMARY,
    DESTRUCTIVE,
}

internal data class BottomButtonTokens(
    val active: ColorToken,
    val onActive: ColorToken,
    val inactive: ColorToken,
    val onInactive: ColorToken,
)

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
fun TelegramMainButton(
    webApp: TelegramWebApp,
    state: ButtonState,
    style: TelegramButtonStyle = TelegramButtonStyle.PRIMARY,
) {
    BottomButton(webApp.mainButton, state, style)
}

@Composable
fun TelegramSecondaryButton(webApp: TelegramWebApp, state: ButtonState) {
    if (webApp.supports(TelegramFeature.SECONDARY_BUTTON)) {
        BottomButton(webApp.secondaryButton, state, TelegramButtonStyle.PRIMARY)
    } else {
        TextButton(state)
    }
}

@Composable
private fun BottomButton(
    button: TelegramBottomButton,
    state: ButtonState,
    style: TelegramButtonStyle,
) {
    if (LocalIsCurrentScreen.current) {
        val spec = state.bottomButtonSpec(state.text.localized(), style.tokens().themed())
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
}

internal fun TelegramButtonStyle.tokens(): BottomButtonTokens {
    return when (this) {
        TelegramButtonStyle.PRIMARY -> BottomButtonTokens(
            active = ColorToken.ButtonActive,
            onActive = ColorToken.OnAction,
            inactive = ColorToken.ButtonInactive,
            onInactive = ColorToken.ActionTextDisabled,
        )

        TelegramButtonStyle.DESTRUCTIVE -> BottomButtonTokens(
            active = ColorToken.Elevated,
            onActive = ColorToken.Error,
            inactive = ColorToken.ButtonInactive,
            onInactive = ColorToken.ActionTextDisabled,
        )
    }
}

@Composable
private fun BottomButtonTokens.themed(): BottomButtonColors {
    return BottomButtonColors(
        active = active.themed.toTelegramColor(),
        onActive = onActive.themed.toTelegramColor(),
        inactive = inactive.themed.toTelegramColor(),
        onInactive = onInactive.themed.toTelegramColor(),
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
