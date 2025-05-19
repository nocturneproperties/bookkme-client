package me.bookk.feature.settings.presentation.passkey

import androidx.compose.runtime.mutableStateListOf
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.designsystem.uistate.AndroidRefreshState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState

class AndroidPasskeyState(
    initData: PasskeyState.InitData
) : PasskeyState {
    override val appBar: AppBarState = AndroidAppBarState(initData.title)
    override val addPasskeyButton: ButtonState = AndroidButtonState(initData.addButtonText)
    override val passkeys: MutableList<PasskeyState.PasskeyItem> = mutableStateListOf()
    override val refresh: RefreshState = AndroidRefreshState()
    override val notification: PresentationNotificationState = AndroidNotificationState()

    override fun replacePasskeyList(items: List<PasskeyState.PasskeyItem>) {
        passkeys.clear()
        passkeys.addAll(items)
    }
}