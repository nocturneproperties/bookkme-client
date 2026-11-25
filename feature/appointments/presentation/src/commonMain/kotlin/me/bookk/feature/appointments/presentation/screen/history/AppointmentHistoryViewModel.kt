package me.bookk.feature.appointments.presentation.screen.history

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.listPager
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.appointments.domain.api.GetAppointmentHistory
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentHistoryQuery
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryDestinations.AppointmentDetails
import org.koin.core.annotation.InjectedParam
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.uuid.Uuid

class AppointmentHistoryViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getAppointmentHistory: GetAppointmentHistory,
    private val dateLocalizer: DateLocalizer,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs,
) : ViewModel(vmArgs) {

    val uiState: AppointmentHistoryState = stateFactory.createAppointmentHistoryState().setup()
    private val searchText = MutableStateFlow("")
    private val historyPages = listPager(
        listState = uiState.appointments,
        notifications = uiState.notifications,
        source = getAppointmentHistory,
        refreshState = uiState.refresh,
    )

    init {
        observeHistory()
        loadHistory()
    }

    private fun observeHistory() {
        searchText
            .flatMapLatest { getAppointmentHistory.flow(businessId, it) }
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderHistory(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderHistory(appointments: List<Appointment>) {
        if (appointments.isEmpty() && uiState.appointments.isInitialLoading) return
        uiState.appointments.replace(mapToItems(appointments))
    }

    private fun loadHistory() {
        searchText
            .map { it.trim().ifBlank { null } }
            .debounce { if (it == null) Duration.ZERO else SEARCH_DEBOUNCE }
            .distinctUntilChanged()
            .map { AppointmentHistoryQuery(businessId, it) }
            .safeOnEach { historyPages.load(it) }
            .observe()
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.searchField.text = query
        searchText.value = query
    }

    private fun onAppointmentClick(appointment: Appointment) {
        uiState.navigation.push(AppointmentDetails(appointment.id))
    }

    private fun mapToItems(appointments: List<Appointment>): List<AppointmentHistoryItemState> {
        val dateFormat = dateLocalizer.forStyle(DateStyle.SHORT)
        return appointments.map { appointment ->
            AppointmentHistoryItemState(
                appointment = appointment,
                formatter = dateFormat,
                onItemClick = weakVMClosure { it.onAppointmentClick(appointment) },
            )
        }
    }

    private fun AppointmentHistoryState.setup() = apply {
        appBar.size = TopBarSize.SMALL
        appBar.title = AppointmentsRes.strings.appointments_history_title.desc()
        appBar.onBackClick =
            weakVMClosure { it.uiState.navigation.push(AppointmentHistoryDestinations.Back) }
        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakVMClosure { vm, value -> vm.onSearchQueryChanged(value) }
        appointments.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = AppointmentsRes.strings.appointments_history_empty.desc(),
        )
    }

    private companion object {
        val SEARCH_DEBOUNCE = 500.milliseconds
    }
}
