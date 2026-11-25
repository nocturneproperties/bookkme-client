package me.bookk.feature.settings.presentation.export

import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
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

enum class UIExportStage {
    REQUEST,
    PREPARING,
    READY,
    FAILED,
    EXPIRED,
}

data class UIExportStatus(
    val title: StringDesc,
    val message: StringDesc?,
    val footer: StringDesc?,
    val icon: ImageResource?,
    val color: ColorToken,
    val isInProgress: Boolean,
    val showsFile: Boolean,
    val stage: UIExportStage,
) {
    internal constructor(stage: UIExportStage, canRequest: Boolean) : this(
        title = stage.title(),
        message = stage.message(),
        footer = stage.footer(canRequest),
        icon = stage.icon(),
        color = stage.color(),
        isInProgress = stage == UIExportStage.PREPARING,
        showsFile = stage == UIExportStage.READY,
        stage = stage,
    )
}

private fun UIExportStage.title(): StringDesc {
    return when (this) {
        UIExportStage.REQUEST -> SettingsRes.strings.settings_export_request_title.desc()
        UIExportStage.PREPARING -> SettingsRes.strings.settings_export_running_title.desc()
        UIExportStage.READY -> SettingsRes.strings.settings_export_ready_title.desc()
        UIExportStage.FAILED -> SettingsRes.strings.settings_export_failed_title.desc()
        UIExportStage.EXPIRED -> SettingsRes.strings.settings_export_expired_title.desc()
    }
}

private fun UIExportStage.message(): StringDesc? {
    return when (this) {
        UIExportStage.REQUEST -> SettingsRes.strings.settings_export_description.desc()
        UIExportStage.PREPARING -> SettingsRes.strings.settings_export_running_message.desc()
        UIExportStage.READY -> null
        UIExportStage.FAILED -> SettingsRes.strings.settings_export_failed_message.desc()
        UIExportStage.EXPIRED -> SettingsRes.strings.settings_export_expired_message.desc()
    }
}

private fun UIExportStage.footer(canRequest: Boolean): StringDesc? {
    return when (this) {
        UIExportStage.PREPARING -> exportRunningFooter.desc()

        UIExportStage.READY -> SettingsRes.strings.settings_export_ready_message.desc()

        UIExportStage.EXPIRED -> null

        UIExportStage.REQUEST,
        UIExportStage.FAILED,
        -> SettingsRes.strings.settings_export_request_footer.desc().takeIf { canRequest }
    }
}

private fun UIExportStage.icon(): ImageResource? {
    return when (this) {
        UIExportStage.PREPARING -> null

        UIExportStage.READY -> DesignSystem.images.export_ready

        UIExportStage.REQUEST,
        UIExportStage.FAILED,
        UIExportStage.EXPIRED,
        -> DesignSystem.images.export_document
    }
}

private fun UIExportStage.color(): ColorToken {
    return when (this) {
        UIExportStage.PREPARING,
        UIExportStage.EXPIRED,
        -> ColorToken.SecondaryText

        UIExportStage.READY -> ColorToken.Success

        UIExportStage.REQUEST,
        UIExportStage.FAILED,
        -> ColorToken.ActionText
    }
}
