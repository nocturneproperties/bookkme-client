package me.bookk.feature.appointments.presentation.screen.requestlist

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.coroutine.resultOnEach
import me.bookk.core.coroutine.resultOnError
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.appointments.domain.api.GetAppointmentsForDashboardBusiness
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.appointments.presentation.screen.requestlist.AppointmentListDestinations.AppointmentDetails
import me.bookk.feature.appointments.presentation.screen.requestlist.AppointmentListDestinations.CreateAppointment

class AppointmentListViewModel(
    private val getAppointmentsForDashboardBusiness: GetAppointmentsForDashboardBusiness,
    private val dateLocalizer: DateLocalizer,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AppointmentListState = stateFactory.createAppointmentListState().setup()

    init {
        observeCurrentBusinessRequests()
    }

    private fun observeCurrentBusinessRequests() {
        getAppointmentsForDashboardBusiness.flow(uiState.selectedDate)
            .flowOn(DispatcherProvider.io)
            .resultOnEach(::mapItems)
            .resultOnError {
                uiState.appointments.replace(emptyList())
                uiState.notifications.add(it.notification())
            }
            .retry()
            .launchIn(viewModelScope)
    }

    private fun onRefresh() {
        val date = uiState.selectedDate
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.refresh.isRefreshing = true },
            call = { getAppointmentsForDashboardBusiness(date) },
            onComplete = ::mapItems,
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = { uiState.refresh.isRefreshing = false },
        )
    }

    private fun onNewAppointmentClick() {
        viewModelScope.launch(DispatcherProvider.io) {
            val businessId = getAppointmentsForDashboardBusiness.businessId() ?: return@launch
            withContext(DispatcherProvider.main) {
                uiState.navigation.push(CreateAppointment(businessId))
            }
        }
    }

    private fun onAppointmentClick(appointment: Appointment) {
        uiState.navigation.push(AppointmentDetails(appointment.id))
    }

    private fun mapItems(appointments: List<Appointment>) {
        val dateFormat = dateLocalizer.forStyle(DateStyle.SHORT)
        val items = appointments.map { appointment ->
            AppointmentItemState(
                appointment = appointment,
                formatter = dateFormat,
                onItemClick = weakSelfClosure { it.onAppointmentClick(appointment) }
            )
        }
        uiState.appointments.replace(items)
    }

    private fun AppointmentListState.setup() = apply {
        appBar.size = TopBarSize.SMALL
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_new.desc(),
                    onClick = weakSelfClosure { it.onNewAppointmentClick() }
                )
            )
        )

        refresh.onRefresh = weakSelfClosure { it.onRefresh() }
        appointments.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = AppointmentsRes.strings.appointments_list_empty.desc(),
        )
    }
}
