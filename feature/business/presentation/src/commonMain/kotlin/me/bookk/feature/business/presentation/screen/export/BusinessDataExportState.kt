package me.bookk.feature.business.presentation.screen.export

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.core.domain.entity.DataExportStatus
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface BusinessDataExportState {
    val appBar: AppBarState
    var status: UIBusinessExportStatus
    var expiry: StringDesc?
    var requestAvailability: StringDesc?
    var error: StringDesc?
    val request: ButtonState
    val download: ButtonState
    val refresh: ButtonState
    val notifications: PresentationNotificationState
    val navigation: NavigationState<BusinessDataExportDestinations>
}

data class UIBusinessExportStatus(
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
        DataExportStatus.RUNNING -> BusinessRes.strings.business_export_running_title.desc()
        DataExportStatus.READY -> BusinessRes.strings.business_export_ready_title.desc()
        DataExportStatus.FAILED -> BusinessRes.strings.business_export_failed_title.desc()
        DataExportStatus.NONE -> BusinessRes.strings.business_export_request_title.desc()
    }
}

private fun DataExportStatus.message(): StringDesc? {
    return when (this) {
        DataExportStatus.RUNNING -> BusinessRes.strings.business_export_running_message.desc()
        DataExportStatus.READY -> null
        DataExportStatus.FAILED -> BusinessRes.strings.business_export_failed_message.desc()
        DataExportStatus.NONE -> BusinessRes.strings.business_export_description.desc()
    }
}

private fun DataExportStatus.footer(canRequest: Boolean): StringDesc? {
    return when (this) {
        DataExportStatus.RUNNING -> BusinessRes.strings.business_export_running_footer.desc()

        DataExportStatus.READY -> BusinessRes.strings.business_export_ready_message.desc()

        DataExportStatus.FAILED,
        DataExportStatus.NONE,
        -> BusinessRes.strings.business_export_request_footer.desc().takeIf { canRequest }
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
