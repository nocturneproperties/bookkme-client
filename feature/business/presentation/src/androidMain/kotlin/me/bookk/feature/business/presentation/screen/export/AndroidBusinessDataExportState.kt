package me.bookk.feature.business.presentation.screen.export

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

internal class AndroidBusinessDataExportState : BusinessDataExportState {
    override val appBar = AndroidAppBarState()
    override var status: UIBusinessExportStatus by mutableStateOf(
        UIBusinessExportStatus(
            title = "".desc(),
            message = null,
            footer = null,
            icon = null,
            color = ColorToken.ActionText,
            isInProgress = false,
            showsFile = false,
        ),
    )
    override var expiry: StringDesc? by mutableStateOf(null)
    override var requestAvailability: StringDesc? by mutableStateOf(null)
    override var error: StringDesc? by mutableStateOf(null)
    override val request = AndroidButtonState()
    override val download = AndroidButtonState()
    override val refresh = AndroidButtonState()
    override val notifications = AndroidNotificationState()
    override val navigation = AndroidNavigationState<BusinessDataExportDestinations>()
}
