package me.bookk.feature.appointments.presentation.screen.requestlist

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.coroutine.resultOnEach
import me.bookk.core.coroutine.resultOnError
import me.bookk.core.now
import me.bookk.core.orNow
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
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.listenFor
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
        listenForUpdates()
    }

    private fun listenForUpdates() {
        listenFor<AppointmentEvent.Created> { onRefresh() }
            .launchIn(viewModelScope)
        listenFor<AppointmentEvent.Updated> { onRefresh() }
            .launchIn(viewModelScope)
    }

    private fun observeCurrentBusinessRequests() {
        getAppointmentsForDashboardBusiness.flow(uiState.datePicker.pickedDate.orNow())
            .flowOn(DispatcherProvider.io)
            .resultOnEach(::mapItems)
            .resultOnError {
                uiState.appointments.replace(emptyList())
                uiState.notifications.add(it.notification())
            }
            .retry()
            .launchIn(viewModelScope)
    }

    private fun onNewDateSelected(date: LocalDate) {
        uiState.datePicker.pickedDate = date
        onRefresh()
    }

    private fun onRefresh() {
        val date = uiState.datePicker.pickedDate.orNow()
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.refresh.isRefreshing = true },
            call = { getAppointmentsForDashboardBusiness(date) },
            onComplete = ::mapItems,
            onError = {
                uiState.appointments.replace(emptyList())
                uiState.notifications.add(it.notification())
            },
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

    private fun onPickDateClick() {
        uiState.datePicker.isDatePickerVisible = true
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
        appBar.title = AppointmentsRes.strings.appointments_title.desc()
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    contentDescription = AppointmentsRes.strings.appointments_pick_date.desc(),
                    icon = DesignSystem.images.date_range,
                    onClick = weakSelfClosure { it.onPickDateClick() }
                ),
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_new.desc(),
                    icon = DesignSystem.images.plus,
                    onClick = weakSelfClosure { it.onNewAppointmentClick() }
                )
            )
        )
        datePicker.pickedDate = LocalDate.now()
        datePicker.onDatePicked = weakSelfClosure { vm, date -> vm.onNewDateSelected(date) }
        refresh.onRefresh = weakSelfClosure { it.onRefresh() }
        appointments.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = AppointmentsRes.strings.appointments_list_empty.desc(),
        )
    }
}
