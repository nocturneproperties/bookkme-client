package me.bookk.feature.authorization.presentation.troubleshoot

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.authorization.resources.AuthRes
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.feature.authorization.presentation.AuthStateFactory
import me.bookk.feature.authorization.presentation.sign_up.state.TroubleshootCardData
import me.bookk.feature.authorization.presentation.sign_up.state.TroubleshootCardData.Reason
import me.bookk.feature.authorization.presentation.troubleshoot.state.TroubleshootEventListener
import me.bookk.feature.authorization.presentation.troubleshoot.state.TroubleshootState

class TroubleshootViewModel(
    stateFactory: AuthStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs), TroubleshootEventListener {

    val uiState = stateFactory.createTroubleshootState(createInitData())

    companion object {
        fun createInitData(): TroubleshootState.InitData {
            return TroubleshootState.InitData(
                title = AuthRes.strings.troubleshoot_title.desc(),
                cardData = TroubleshootCardData(
                    title = AuthRes.strings.troubleshoot_passkey_troubleshoot.desc(),
                    icon = AuthRes.images.passkey,
                    reasons = listOf(
                        Reason(
                            id = 0,
                            title = AuthRes.strings.troubleshoot_passkey_lost.desc(),
                            description = AuthRes.strings.troubleshoot_passkey_lost_description.desc()
                        ),
                        Reason(
                            id = 1,
                            title = AuthRes.strings.troubleshoot_passkey_missing.desc(),
                            description = AuthRes.strings.troubleshoot_passkey_missing_description.desc()
                        )
                    )
                ),
                buttonText = AuthRes.strings.troubleshoot_contact_support_button.desc()
            )
        }
    }
}