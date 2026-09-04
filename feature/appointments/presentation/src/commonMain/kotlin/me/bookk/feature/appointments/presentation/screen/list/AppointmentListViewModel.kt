package me.bookk.feature.appointments.presentation.screen.list

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retry
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.orNow
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.date.startOfWeek
import me.bookk.core.presentation.date.today
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadCachedList
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.GetAppointmentRequests
import me.bookk.feature.appointments.domain.api.GetAppointmentsForBusiness
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.listenFor
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.appointments.presentation.screen.list.AppointmentListDestinations.AppointmentDetails
import me.bookk.feature.appointments.presentation.screen.list.AppointmentListDestinations.CreateAppointment
import kotlin.uuid.Uuid

class AppointmentListViewModel(
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    private val getAppointmentsForBusiness: GetAppointmentsForBusiness,
    private val getAppointmentRequests: GetAppointmentRequests,
    private val dateLocalizer: DateLocalizer,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private val shortWeekdayFormat = dateLocalizer.forStyle(DateStyle.NARROW_WEEKDAY)

    val uiState: AppointmentListState = stateFactory.createAppointmentListState().setup()

    private var businessId = Uuid.random()

    init {
        observeCurrentBusiness()
        listenForUpdates()
    }

    private fun listenForUpdates() {
        listenFor<AppointmentEvent.Created> { onRefresh() }
            .launchIn(viewModelScope)
        listenFor<AppointmentEvent.Updated> { onRefresh() }
            .launchIn(viewModelScope)
    }

    private fun observeCurrentBusiness() {
        observeCurrentBusinessId()
            .flowOn(DispatcherProvider.io)
            .filterNotNull()
            .distinctUntilChanged()
            .onEach {
                businessId = it
                if (uiState.requestsBusinessId != null) {
                    uiState.requestsBusinessId = it
                }
                loadAppointments(it)
                loadRequestCount(it)
            }
            .retry()
            .launchIn(viewModelScope)
    }

    private fun loadAppointments(businessId: Uuid) {
        val date = uiState.datePicker.pickedDate.orNow()
        loadCachedList(
            listState = uiState.appointments,
            refreshState = uiState.refresh,
            call = { getAppointmentsForBusiness.cached(businessId, date, it) },
            onComplete = ::mapItems,
        )
    }

    private fun loadRequestCount(businessId: Uuid) {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.requestsButton.startLoading() },
            call = { getAppointmentRequests(businessId).size },
            onComplete = {
                uiState.requestsButton.text =
                    AppointmentsRes.strings.appointments_requests_count.format(it)
            },
            onError = { /* Silently ignore */ },
            onTerminate = { uiState.requestsButton.stopLoading() }
        )
    }

    private fun onNewDateSelected(date: LocalDate) {
        uiState.datePicker.pickedDate = date
        uiState.appointments.isInitialLoading = true
        loadAppointments(businessId)
        uiState.dates.replace(createDateInfoFrom(date))
    }

    private fun createDateInfoFrom(date: LocalDate): List<DateInfo> {
        val startOfWeek = date.startOfWeek()
        val today = LocalDate.today()
        return DayOfWeek.entries.map {
            val weekDay = startOfWeek.plus(it.isoDayNumber - 1, DateTimeUnit.DAY)
            DateInfo(
                date = weekDay,
                str = shortWeekdayFormat.format(weekDay),
                isToday = weekDay == today
            )
        }
    }

    private fun onRefresh() {
        loadAppointments(businessId)
        loadRequestCount(businessId)
    }

    private fun onNewAppointmentClick() {
        uiState.navigation.push(CreateAppointment(businessId))
    }

    private fun showRequestsScreen() {
        uiState.requestsBusinessId = businessId
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
                onItemClick = weakVMClosure { it.onAppointmentClick(appointment) }
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
                    onClick = weakVMClosure { it.onPickDateClick() }
                ),
                AppBarAction(
                    contentDescription = DesignSystem.strings.action_new.desc(),
                    icon = DesignSystem.images.plus,
                    onClick = weakVMClosure { it.onNewAppointmentClick() }
                )
            )
        )

        requestsButton.text = AppointmentsRes.strings.appointments_requests_count.format(0)
        requestsButton.onClick = weakVMClosure { it.showRequestsScreen() }

        datePicker.pickedDate = LocalDate.today()
        dates.replace(createDateInfoFrom(LocalDate.today()))
        datePicker.onDatePicked = weakVMClosure { vm, date -> vm.onNewDateSelected(date) }
        refresh.onRefresh = weakVMClosure { it.onRefresh() }
        appointments.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = AppointmentsRes.strings.appointments_list_empty.desc(),
        )
    }
}
