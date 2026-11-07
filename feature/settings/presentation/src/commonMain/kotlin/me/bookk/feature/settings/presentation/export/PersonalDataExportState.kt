package me.bookk.feature.settings.presentation.export

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface PersonalDataExportState {
    val appBar: AppBarState
    var status: UIExportStatus
    var expiry: StringDesc?
    var requestAvailability: StringDesc?
    var error: StringDesc?
    val request: ButtonState
    val download: ButtonState
    val retry: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<PersonalDataExportDestinations>
}

data class UIExportStatus(
    val title: StringDesc,
    val message: StringDesc?,
    val footer: StringDesc?,
    val icon: ImageResource?,
    val color: ColorToken,
    val isInProgress: Boolean,
    val showsFile: Boolean,
) {
    internal constructor(status: DataExportStatus, canRequest: Boolean) : this(
        title = status.title(),
        message = status.message(),
        footer = status.footer(canRequest),
        icon = status.icon(),
        color = status.color(),
        isInProgress = status == DataExportStatus.RUNNING,
        showsFile = status == DataExportStatus.READY,
    )
}

private fun DataExportStatus.title(): StringDesc {
    return when (this) {
        DataExportStatus.RUNNING -> SettingsRes.strings.settings_export_running_title.desc()
        DataExportStatus.READY -> SettingsRes.strings.settings_export_ready_title.desc()
        DataExportStatus.FAILED -> SettingsRes.strings.settings_export_failed_title.desc()
        DataExportStatus.NONE -> SettingsRes.strings.settings_export_request_title.desc()
    }
}

private fun DataExportStatus.message(): StringDesc? {
    return when (this) {
        DataExportStatus.RUNNING -> SettingsRes.strings.settings_export_running_message.desc()
        DataExportStatus.READY -> null
        DataExportStatus.FAILED -> SettingsRes.strings.settings_export_failed_message.desc()
        DataExportStatus.NONE -> SettingsRes.strings.settings_export_description.desc()
    }
}

private fun DataExportStatus.footer(canRequest: Boolean): StringDesc? {
    return when (this) {
        DataExportStatus.RUNNING -> SettingsRes.strings.settings_export_running_footer.desc()

        DataExportStatus.READY -> SettingsRes.strings.settings_export_ready_message.desc()

        DataExportStatus.FAILED,
        DataExportStatus.NONE,
        -> SettingsRes.strings.settings_export_request_footer.desc().takeIf { canRequest }
    }
}

private fun DataExportStatus.icon(): ImageResource? {
    return when (this) {
        DataExportStatus.RUNNING -> null

        DataExportStatus.READY -> DesignSystem.images.export_ready

        DataExportStatus.FAILED,
        DataExportStatus.NONE,
        -> DesignSystem.images.export_document
    }
}

private fun DataExportStatus.color(): ColorToken {
    return when (this) {
        DataExportStatus.RUNNING -> ColorToken.SecondaryText

        DataExportStatus.READY -> ColorToken.Success

        DataExportStatus.FAILED,
        DataExportStatus.NONE,
        -> ColorToken.ActionText
    }
}
