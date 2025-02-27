package me.bookk.feature.settings.presentation.contactus

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.SwitchState
import me.bookk.designsystem.uistate.TextFieldState

interface ContactUsState {

    val appBar: AppBarState
    val contactField: TextFieldState
    val includeLogsSwitch: SwitchState
    val logsExplanationText: StringDesc
    val submitButton: ButtonState

    val notifications: PresentationNotificationState

    data class InitData(
        val title: StringDesc,
        val contactHint: StringDesc,
        val usageLogsText: StringDesc,
        val includeLogsExplanation: StringDesc,
        val submitButtonText: StringDesc
    )
}