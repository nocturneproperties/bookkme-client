package me.bookk.feature.services.presentation.group.list

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup

interface ServiceGroupListState {
    val appBar: AppBarState
    val search: TextFieldState
    val groups: ListState<ServiceGroupUI>
    val refreshState: RefreshState
    var isAddGroupDialogVisible: Boolean
    val notifications: PresentationNotificationState
    val navigation: NavigationState<ServiceGroupListDestination>

    data class ServiceGroupUI(
        val id: String,
        val name: String,
        val onItemClick: () -> Unit,
    )
}

internal fun ServiceGroup.ui(onItemClick: () -> Unit) = ServiceGroupListState.ServiceGroupUI(
    id = id.toString(),
    name = name,
    onItemClick = onItemClick
)
