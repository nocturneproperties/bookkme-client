package library.telegram

import kotlinx.coroutines.suspendCancellableCoroutine
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import kotlin.coroutines.resume

private const val MAX_TITLE_LENGTH = 64
private const val MAX_MESSAGE_LENGTH = 256
private const val MAX_BUTTON_TEXT_LENGTH = 64
private const val MAX_BUTTONS = 3
private const val ACTION_BUTTON_ID = "action"
private const val CLOSE_BUTTON_ID = ""

external interface TelegramPopupParams {
    var title: String?
    var message: String
    var buttons: Array<TelegramPopupButtonParams>
}

external interface TelegramPopupButtonParams {
    var id: String
    var type: String
    var text: String?
}

enum class TelegramPopupButtonType(
    val value: String,
) {
    DEFAULT("default"),
    DESTRUCTIVE("destructive"),
    CLOSE("close"),
}

class TelegramPopupButton private constructor(
    val id: String,
    val type: TelegramPopupButtonType,
    val text: String,
    val onClick: () -> Unit,
) {
    companion object {
        fun of(descriptor: ButtonDescriptor, text: String): TelegramPopupButton {
            return TelegramPopupButton(
                id = descriptor.id.toString(),
                type = descriptor.actionType.popupButtonType(),
                text = text.limitTo(MAX_BUTTON_TEXT_LENGTH),
                onClick = descriptor.onClick,
            )
        }

        fun action(text: String, onClick: () -> Unit): TelegramPopupButton {
            return TelegramPopupButton(
                id = ACTION_BUTTON_ID,
                type = TelegramPopupButtonType.DEFAULT,
                text = text.limitTo(MAX_BUTTON_TEXT_LENGTH),
                onClick = onClick,
            )
        }

        fun close(): TelegramPopupButton {
            return TelegramPopupButton(CLOSE_BUTTON_ID, TelegramPopupButtonType.CLOSE, "", {})
        }

        private fun ActionType.popupButtonType(): TelegramPopupButtonType {
            return if (this == ActionType.NEGATIVE) {
                TelegramPopupButtonType.DESTRUCTIVE
            } else {
                TelegramPopupButtonType.DEFAULT
            }
        }
    }
}

class TelegramPopup private constructor(
    val title: String?,
    val message: String,
    val buttons: List<TelegramPopupButton>,
) {
    fun onClosed(buttonId: String?) {
        if (buttonId.isNullOrEmpty()) return
        buttons.firstOrNull { it.id == buttonId }?.onClick?.invoke()
    }

    fun toParams(): TelegramPopupParams {
        return js("{}").unsafeCast<TelegramPopupParams>().also { params ->
            title?.let { params.title = it }
            params.message = message
            params.buttons = buttons.map { it.toParams() }.toTypedArray()
        }
    }

    private fun TelegramPopupButton.toParams(): TelegramPopupButtonParams {
        return js("{}").unsafeCast<TelegramPopupButtonParams>().also { params ->
            params.id = id
            params.type = type.value
            if (type != TelegramPopupButtonType.CLOSE) params.text = text
        }
    }

    companion object {
        fun create(
            title: String?,
            message: String,
            buttons: List<TelegramPopupButton>,
        ): TelegramPopup {
            return TelegramPopup(
                title = title?.limitTo(MAX_TITLE_LENGTH)?.takeIf { it.isNotEmpty() },
                message = message.limitTo(MAX_MESSAGE_LENGTH),
                buttons = buttons.take(MAX_BUTTONS).ifEmpty { listOf(TelegramPopupButton.close()) },
            )
        }

        fun globalMessage(
            text: String,
            actionText: String?,
            onAction: (() -> Unit)?,
        ): TelegramPopup {
            val action = actionText?.let { TelegramPopupButton.action(it) { onAction?.invoke() } }
            return create(null, text, listOfNotNull(action, TelegramPopupButton.close()))
        }
    }
}

suspend fun TelegramWebApp.show(popup: TelegramPopup) {
    if (!supports(TelegramFeature.POPUP)) return
    val buttonId = suspendCancellableCoroutine { continuation ->
        showPopup(popup.toParams()) { continuation.resume(it) }
    }
    popup.onClosed(buttonId)
}
