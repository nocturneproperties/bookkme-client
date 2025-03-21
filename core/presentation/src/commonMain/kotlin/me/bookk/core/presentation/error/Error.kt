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
        val text: StringDesc
    ) : PresentationNotification

    data class Message(
        val title: StringDesc? = null,
        val message: StringDesc,
        val buttons: List<ButtonDescriptor>
    ) : PresentationNotification
}

class ButtonDescriptor(
    val text: StringDesc,
    val actionType: ActionType = ActionType.POSITIVE,
    val onClick: () -> Unit = {}
) {
    @OptIn(ExperimentalUuidApi::class)
    @UsedInSwift
    val id: Int = Uuid.random().hashCode()

    enum class ActionType {
        POSITIVE,
        NEGATIVE
    }
}