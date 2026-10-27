package me.bookk.feature.settings.presentation.contactus

import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidTextFieldState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

@Immutable
internal class AndroidContactUsState(
    initData: ContactUsState.InitData
) : ContactUsState {
    override val appBar: AppBarState = AndroidAppBarState(title = initData.title)
    override val contactField: TextFieldState = AndroidTextFieldState(placeholder = initData.contactHint)
    override val includeLogsSwitch: BooleanState = AndroidBooleanState(text = initData.usageLogsText, isChecked = false)
    override val logsExplanationText: StringDesc = initData.includeLogsExplanation
    override val submitButton: ButtonState = AndroidButtonState(text = initData.submitButtonText, isEnabled = false)

    override val notifications: PresentationNotificationState = AndroidNotificationState()
    override val navigation: NavigationState<ContactUsNavigationDestination> = AndroidNavigationState()
}