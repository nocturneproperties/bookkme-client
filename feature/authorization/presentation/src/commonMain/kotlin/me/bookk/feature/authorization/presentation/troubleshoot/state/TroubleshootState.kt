package me.bookk.feature.authorization.presentation.troubleshoot.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.feature.authorization.presentation.sign_up.state.TroubleshootCardData
import me.bookk.feature.authorization.presentation.troubleshoot.TroubleshootNavigationDestination

interface TroubleshootState {
    val appBar: AppBarState
    val troubleshootCardStaticData: TroubleshootCardData
    val contactSupportButton: ButtonState

    val navigation: NavigationState<TroubleshootNavigationDestination>

    class InitData(
        val title: StringDesc,
        val cardData: TroubleshootCardData,
        val buttonText: StringDesc,
    )
}