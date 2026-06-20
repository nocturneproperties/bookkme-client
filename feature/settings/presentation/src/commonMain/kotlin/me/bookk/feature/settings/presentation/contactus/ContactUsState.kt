package me.bookk.feature.settings.presentation.contactus

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface ContactUsState {

    val appBar: AppBarState
    val contactField: TextFieldState
    val includeLogsSwitch: BooleanState
    val logsExplanationText: StringDesc
    val submitButton: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<ContactUsNavigationDestination>

    data class InitData(
        val title: StringDesc,
        val contactHint: StringDesc,
        val usageLogsText: StringDesc,
        val includeLogsExplanation: StringDesc,
        val submitButtonText: StringDesc
    )
}