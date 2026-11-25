package me.bookk.feature.settings.presentation.export

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState

internal class WebPersonalDataExportState : PersonalDataExportState {
    override val appBar = WebAppBarState()
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
    override val request = WebButtonState()
    override val download = WebButtonState()
    override val retry = WebButtonState()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<PersonalDataExportDestinations>()
}
