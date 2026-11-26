package me.bookk.feature.settings.presentation.export

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidButtonState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState
import me.bookk.feature.settings.presentation.confirmation.AndroidChatConfirmationState

internal class AndroidPersonalDataExportState : PersonalDataExportState {
    override val appBar = AndroidAppBarState()
    override var status: UIExportStatus by mutableStateOf(
        UIExportStatus(
            title = "".desc(),
            message = null,
            footer = null,
            icon = null,
            color = ColorToken.ActionText,
            isInProgress = false,
            showsFile = false,
            stage = UIExportStage.REQUEST,
        ),
    )
    override var expiry: StringDesc? by mutableStateOf(null)
    override var requestAvailability: StringDesc? by mutableStateOf(null)
    override var error: StringDesc? by mutableStateOf(null)
    override val request = AndroidButtonState()
    override val chatConfirmation = AndroidChatConfirmationState()
    override val download = AndroidButtonState()
    override val retry = AndroidButtonState()
    override val notifications = AndroidNotificationState()
    override val navigation = AndroidNavigationState<PersonalDataExportDestinations>()
}
