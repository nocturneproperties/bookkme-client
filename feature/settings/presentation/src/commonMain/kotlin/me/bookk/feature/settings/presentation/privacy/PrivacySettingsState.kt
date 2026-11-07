package me.bookk.feature.settings.presentation.privacy

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.BooleanState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface PrivacySettingsState {
    val appBar: AppBarState
    var diagnosticsTitle: StringDesc
    val crashReporting: BooleanState
    var crashReportingDescription: StringDesc
    val legal: LegalDocumentsSection
    val notifications: PresentationNotificationState
    val navigation: NavigationState<PrivacySettingsDestinations>
}
