package me.bookk.feature.services.presentation.service.add

import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.CheckBoxState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PickerPresentation
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup

interface AddServiceState {

    val appBar: AppBarState

    val group: PickerFieldState<GroupUI>
    val name: TextFieldState
    val duration: TextFieldState
    val price: TextFieldState
    val enabled: CheckBoxState
    val create: ButtonState

    val notifications: PresentationNotificationState
    val navigation: NavigationState<AddServiceDestination>

    data class GroupUI(
        val domain: ServiceGroup
    ) : PickerPresentation() {
        override val pickerItemId: String = domain.id.toString()
        override val displayName: StringDesc = domain.name.desc()
    }
}