package me.bookk.designsystem

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.resources.DesignSystem

fun PresentationNotification.Message.Companion.simple(
    message: StringDesc,
    onCanceled: () -> Unit = {}
): PresentationNotification.Message {
    return PresentationNotification.Message(
        message = message,
        buttons = listOf(
            ButtonDescriptor(
                DesignSystem.strings.action_dismiss.desc(),
                onClick = onCanceled
            ),
        )
    )
}

fun PresentationNotification.Message.Companion.deleteConfirmation(
    message: StringDesc = DesignSystem.strings.message_delete.desc(),
    onConfirmed: () -> Unit,
    onCanceled: () -> Unit = {}
): PresentationNotification.Message {
    return PresentationNotification.Message(
        title = DesignSystem.strings.action_confirm.desc(),
        message = message,
        buttons = listOf(
            ButtonDescriptor(
                DesignSystem.strings.action_cancel.desc(),
                onClick = onCanceled
            ),
            ButtonDescriptor(
                DesignSystem.strings.action_delete.desc(),
                actionType = ActionType.NEGATIVE,
                onClick = onConfirmed
            )
        )
    )
}