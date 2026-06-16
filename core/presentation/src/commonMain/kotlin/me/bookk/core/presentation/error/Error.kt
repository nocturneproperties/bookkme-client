package me.bookk.core.presentation.error

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.core.UsedInSwift
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface PresentationNotification {
    data object Ignore : PresentationNotification
    data object Unauthorized : PresentationNotification

    //Message that displayed on top of everything even if you navigate between screens
    data class GlobalMessage(
        val text: StringDesc,
        val state: State = State.SUCCESS,
        val duration: Duration = Duration.SHORT,
        val actionText: StringDesc? = null,
        val onActionClick: (() -> Unit)? = null,
    ) : PresentationNotification {

        val id: String = Uuid.random().toHexString()

        enum class State {
            ERROR,
            SUCCESS,
            INFO
        }

        enum class Duration {
            SHORT,
            LONG,
            INDEFINITE
        }
    }

    data class Message(
        val title: StringDesc? = null,
        val message: StringDesc,
        val buttons: List<ButtonDescriptor>
    ) : PresentationNotification {
        val id: String = Uuid.random().toHexString()
        companion object
    }

    data class InputMessage(
        val title: StringDesc,
        val message: StringDesc,
        val placeholder: StringDesc? = null,
        val initialText: String = "",
        val cancelText: StringDesc,
        val confirmText: StringDesc,
        val confirmActionType: ActionType = ActionType.POSITIVE,
        val onConfirm: (String) -> Unit,
        val onCancel: () -> Unit = {}
    ) : PresentationNotification {
        val id: String = Uuid.random().toHexString()
        companion object
    }
}

class ButtonDescriptor(
    val text: StringDesc,
    val actionType: ActionType = ActionType.POSITIVE,
    val onClick: () -> Unit = {}
) {
    @OptIn(ExperimentalUuidApi::class)
    @UsedInSwift
    val id: Int = Uuid.random().hashCode()
}

enum class ActionType {
    POSITIVE,
    NEGATIVE,
    CANCEL,
    CONFIRM
}