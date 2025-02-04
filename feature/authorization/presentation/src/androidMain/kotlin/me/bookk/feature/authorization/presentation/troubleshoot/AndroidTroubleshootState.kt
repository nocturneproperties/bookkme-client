package me.bookk.feature.authorization.presentation.troubleshoot

import androidx.compose.runtime.Immutable
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.authorization.presentation.sign_up.state.TroubleshootCardData
import me.bookk.feature.authorization.presentation.troubleshoot.state.TroubleshootState

@Immutable
internal class AndroidTroubleshootState(
    override val appBar: AppBarState,
    override val troubleshootCardStaticData: TroubleshootCardData,
    override val contactSupportButton: ButtonState,
    override val navigation: NavigationState<TroubleshootNavigationDestination>
) : TroubleshootState {
    constructor(initData: TroubleshootState.InitData) : this(
        appBar = AndroidAppBarState(title = initData.title),
        troubleshootCardStaticData = initData.cardData,
        contactSupportButton = AndroidButtonState(text = initData.buttonText),
        navigation = AndroidNavigationState()
    )
}