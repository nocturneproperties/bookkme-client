package me.bookk.feature.authorization.presentation.sign_in.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState

interface SignInState {
    val appBar: AppBarState
    val passkeyCard: TroubleshootPasskeyCardData
    val learnMoreButton: ButtonState
    val signInButton: ButtonState

    class InitData(
        val title: StringDesc,
        val passkeyCardData: TroubleshootPasskeyCardData,
        val learnMoreText: StringDesc,
        val buttonText: StringDesc
    )
}