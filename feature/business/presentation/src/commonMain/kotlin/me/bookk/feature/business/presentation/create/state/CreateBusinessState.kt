package me.bookk.feature.business.presentation.create.state

import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState

interface CreateBusinessState {
    val appBar: AppBarState
    val name: TextFieldState
    val createBtn: ButtonState

    val notifications: PresentationNotificationState

    class InitData(
        val title: StringDesc,
        val hint: StringDesc,
        val supportingText: StringDesc,
        val buttonText: StringDesc,
        val maxNameLength: Int
    )
}