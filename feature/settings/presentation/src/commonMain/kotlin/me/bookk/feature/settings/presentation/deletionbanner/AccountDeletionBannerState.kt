package me.bookk.feature.settings.presentation.deletionbanner

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState

interface AccountDeletionBannerState {
    var isVisible: Boolean
    var message: StringDesc
    val cancelButton: ButtonState
    val notifications: PresentationNotificationState
}
