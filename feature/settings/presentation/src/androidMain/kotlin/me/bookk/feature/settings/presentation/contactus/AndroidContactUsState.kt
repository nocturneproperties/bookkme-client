package me.bookk.feature.settings.presentation.contactus

import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidPresentationNotificationState
import me.bookk.designsystem.uistate.AndroidSwitchState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.SwitchState
import me.bookk.designsystem.uistate.TextFieldState

@Immutable
internal class AndroidContactUsState(
    initData: ContactUsState.InitData
) : ContactUsState {
    override val appBar: AppBarState = AndroidAppBarState(title = initData.title)
    override val contactField: TextFieldState = AndroidTextFieldState(hint = initData.contactHint)
    override val includeLogsSwitch: SwitchState = AndroidSwitchState(text = initData.usageLogsText, isChecked = false)
    override val logsExplanationText: StringDesc = initData.includeLogsExplanation
    override val submitButton: ButtonState = AndroidButtonState(text = initData.submitButtonText, isEnabled = false)

    override val notifications: PresentationNotificationState = AndroidPresentationNotificationState()
    override val navigation: NavigationState<ContactUsNavigationDestination> = AndroidNavigationState()
}