package me.bookk.designsystem

import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.error.ButtonDescriptor
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.resources.DesignSystem

fun PresentationNotification.Message.Companion.deleteConfirmation(
    onConfirmed: () -> Unit,
    onCanceled: () -> Unit = {}
): PresentationNotification.Message {
    return PresentationNotification.Message(
        title = DesignSystem.strings.action_confirm.desc(),
        message = DesignSystem.strings.message_delete.desc(),
        buttons = listOf(
            ButtonDescriptor(
                DesignSystem.strings.action_cancel.desc(),
                onClick = onCanceled
            ),
            ButtonDescriptor(
                DesignSystem.strings.action_delete.desc(),
                actionType = ButtonDescriptor.ActionType.NEGATIVE,
                onClick = onConfirmed
            )
        )
    )
}