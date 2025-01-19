package me.bookk.feature.authorization.presentation.sign_in.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.ViewState

interface SignInState {
    val appBar: AppBarState
    val troubleshootCardStaticData: TroubleshootCardData
    val troubleshootView: ViewState
    val learnMoreButton: ButtonState
    val signInButton: ButtonState

    class InitData(
        val title: StringDesc,
        val learnMoreText: StringDesc,
        val buttonText: StringDesc,
        val troubleshootCardStaticData: TroubleshootCardData,
        val isTroubleshootCardVisible: Boolean
    )
}