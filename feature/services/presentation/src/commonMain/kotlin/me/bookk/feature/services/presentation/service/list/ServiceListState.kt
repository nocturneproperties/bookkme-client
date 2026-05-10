package me.bookk.feature.services.presentation.service.list

import me.bookk.designsystem.uistate.AppBarState
import me.bookk.designsystem.uistate.ListState
import me.bookk.designsystem.uistate.NavigationState
import me.bookk.designsystem.uistate.PresentationNotificationState
import me.bookk.designsystem.uistate.RefreshState
import me.bookk.designsystem.uistate.TextFieldState
import me.bookk.feature.services.domain.api.service.entity.Service

interface ServiceListState {
    val appBar: AppBarState
    val searchField: TextFieldState

    val refreshState: RefreshState
    val services: ListState<ServiceGroupUI>

    val notifications: PresentationNotificationState
    val navigation: NavigationState<ServiceListDestination>

    class ServiceGroupUI(
        val id: String,
        val name: String,
        val onItemClick: (ServiceUI) -> Unit,
        val items: List<ServiceUI>
    )

    class ServiceUI internal constructor(
        service: Service
    ) {
        val id: String = service.id.toString()
        val title: String = service.name
        internal val domain = service
    }
}