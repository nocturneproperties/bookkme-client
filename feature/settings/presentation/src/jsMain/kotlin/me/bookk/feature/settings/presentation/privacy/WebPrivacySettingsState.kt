package me.bookk.feature.settings.presentation.privacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.WebAppBarState
import me.bookk.designsystem.uistate.WebBooleanState
import me.bookk.designsystem.uistate.WebButtonState
import me.bookk.designsystem.uistate.WebNavigationState
import me.bookk.designsystem.uistate.WebNotificationState

internal class WebPrivacySettingsState : PrivacySettingsState {
    override val appBar = WebAppBarState()
    override var diagnosticsTitle: StringDesc by mutableStateOf("".desc())
    override val crashReporting = WebBooleanState()
    override var crashReportingDescription: StringDesc by mutableStateOf("".desc())
    override val legal = WebLegalDocumentsSection()
    override val notifications = WebNotificationState()
    override val navigation = WebNavigationState<PrivacySettingsDestinations>()
}

internal class WebLegalDocumentsSection : LegalDocumentsSection {
    override var title: StringDesc by mutableStateOf("".desc())
    override var documents: List<LegalDocumentItem> by mutableStateOf(emptyList())
    override var isLoading: Boolean by mutableStateOf(false)
    override var message: StringDesc? by mutableStateOf(null)
    override val accept = WebButtonState()
    override val retry = WebButtonState()
}
