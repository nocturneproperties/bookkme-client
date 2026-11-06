package me.bookk.feature.settings.presentation.privacy

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AndroidAppBarState
import me.bookk.designsystem.uistate.AndroidBooleanState
import me.bookk.designsystem.uistate.AndroidNavigationState
import me.bookk.designsystem.uistate.AndroidNotificationState

internal class AndroidPrivacySettingsState : PrivacySettingsState {
    override val appBar = AndroidAppBarState()
    override var diagnosticsTitle: StringDesc by mutableStateOf("".desc())
    override val crashReporting = AndroidBooleanState()
    override var crashReportingDescription: StringDesc by mutableStateOf("".desc())
    override val legal = AndroidLegalDocumentsSectionState()
    override val notifications = AndroidNotificationState()
    override val navigation = AndroidNavigationState<PrivacySettingsDestinations>()
}
