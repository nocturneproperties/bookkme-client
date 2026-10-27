package me.bookk.feature.appointments.presentation.screen.list

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.date.startOfWeek
import me.bookk.core.presentation.date.today
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.convenience.resetListOnChange
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.appointments.domain.api.GetOwnAppointmentRequests
import me.bookk.feature.appointments.domain.api.GetAppointmentsForBusiness
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusiness
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.CurrentBusiness
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.appointments.presentation.screen.list.AppointmentListDestinations.AppointmentDetails
import me.bookk.feature.appointments.presentation.screen.list.AppointmentListDestinations.CreateAppointment
import kotlin.uuid.Uuid

class AppointmentListViewModel(
    private val observeCurrentBusiness: ObserveCurrentBusiness,
    private val getAppointmentsForBusiness: GetAppointmentsForBusiness,
    private val getOwnAppointmentRequests: GetOwnAppointmentRequests,
    private val dateLocalizer: DateLocalizer,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private val shortWeekdayFormat = dateLocalizer.forStyle(DateStyle.NARROW_WEEKDAY)

    val uiState: AppointmentListState = stateFactory.createAppointmentListState().setup()

    private var businessId = Uuid.random()
    private val selectedDate = MutableStateFlow(LocalDate.today())

    init {
        observeAppointments()
        observeBusinessId()
        observeAppointmentsKey()
        observeRequestCount()
    }

    private fun currentBusiness(): Flow<CurrentBusiness> {
        return observeCurrentBusiness()
            .filterNotNull()
            .distinctUntilChanged()
    }

    private fun currentBusinessId(): Flow<Uuid> {
        return currentBusiness()
            .map { it.id }
            .distinctUntilChanged()
    }

    private fun observeAppointments() {
        combine(currentBusiness(), selectedDate, ::Pair)
            .flatMapLatest { (business, date) -> employeeAppointments(business, date) }
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderAppointments(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun employeeAppointments(business: CurrentBusiness, date: LocalDate): Flow<List<Appointment>> {
        val employeeId = business.employeeId ?: return flowOf(emptyList())
        return getAppointmentsForBusiness.flow(date, employeeId)
    }

    private fun renderAppointments(appointments: List<Appointment>) {
        if (appointments.isEmpty() && uiState.appointments.isInitialLoading) return
        mapItems(appointments)
    }

    private fun observeBusinessId() {
        currentBusinessId()
            .flowOn(DispatcherProvider.io)
            .safeOnEach {
                businessId = it
                refreshRequests(it)
            }
            .observe()
    }

    private fun observeRequestCount() {
        currentBusinessId()
            .flatMapLatest { getOwnAppointmentRequests.flow(it) }
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderRequestCount(it.size) }
            .observe()
    }

    private fun renderRequestCount(count: Int) {
        uiState.requestsButton.text = AppointmentsRes.strings.appointments_requests_count.format(count)
    }

    private fun observeAppointmentsKey() {
        combine(currentBusiness(), selectedDate, ::Pair)
            .flowOn(DispatcherProvider.io)
            .resetListOnChange(uiState.appointments)
            .safeOnEach { (business, date) -> loadAppointments(business, date) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadAppointments(business: CurrentBusiness, date: LocalDate) {
        loadList(
            listState = uiState.appointments,
            notifications = uiState.notifications,
            refreshState = uiState.refresh,
            onRefresh = weakVMClosure { it.refreshRequests(it.businessId) },
            call = { refreshEmployeeAppointments(business, date) }
        )
    }

    private suspend fun refreshEmployeeAppointments(business: CurrentBusiness, date: LocalDate): List<Appointment> {
        val employeeId = business.employeeId ?: return emptyList()
        return getAppointmentsForBusiness.refresh(business.id, date, employeeId)
    }

    private fun refreshRequests(businessId: Uuid) {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.requestsButton.startLoading() },
            call = { getOwnAppointmentRequests.refresh(businessId) },
            onError = { /* Silently ignore */ },
            onTerminate = { uiState.requestsButton.stopLoading() }
        )
    }

    private fun onNewDateSelected(date: LocalDate) {
        if (selectedDate.value == date) return
        uiState.datePicker.pickedDate = date
        selectedDate.value = date
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

    private fun onNewAppointmentClick() {
        uiState.navigation.push(CreateAppointment(businessId))
    }

    private fun showRequestsScreen() {
        uiState.isRequestsVisible = true
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
        appointments.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = AppointmentsRes.strings.appointments_list_empty.desc(),
        )
    }
}
