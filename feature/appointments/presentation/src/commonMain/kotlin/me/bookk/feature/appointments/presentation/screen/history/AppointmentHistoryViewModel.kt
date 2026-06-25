package me.bookk.feature.appointments.presentation.screen.history

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.LaunchBehaviour
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.date.DateStyle
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.TopBarSize
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.appointments.domain.api.GetAppointmentHistory
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.presentation.AppointmentsStateFactory
import me.bookk.feature.appointments.presentation.screen.history.AppointmentHistoryDestinations.AppointmentDetails
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class AppointmentHistoryViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getAppointmentHistory: GetAppointmentHistory,
    private val dateLocalizer: DateLocalizer,
    stateFactory: AppointmentsStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: AppointmentHistoryState = stateFactory.createAppointmentHistoryState().setup()
    private val searchQuery = MutableStateFlow<String?>(null)

    init {
        loadHistory()
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        searchQuery
            .drop(1)
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .flowOn(DispatcherProvider.io)
            .onEach { loadHistory(it) }
            .launchIn(viewModelScope)
    }

    private fun loadHistory(query: String? = null, isRefresh: Boolean = false) {
        launch(
            key = LOAD_KEY,
            launchIn = DispatcherProvider.io,
            onStart = {
                if (isRefresh) uiState.refresh.isRefreshing = true
                else uiState.appointments.isInitialLoading = true
            },
            call = { getAppointmentHistory.reload(businessId, query) },
            onComplete = { uiState.appointments.replace(mapToItems(it)) },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = {
                if (isRefresh) uiState.refresh.isRefreshing = false
                else uiState.appointments.isInitialLoading = false
            }
        )
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.searchField.text = query
        searchQuery.value = query.trim().ifBlank { null }
    }

    private fun onRefresh() {
        loadHistory(uiState.searchField.text.ifBlank { null }, isRefresh = true)
    }

    private fun onLoadMore() {
        launch(
            key = LOAD_KEY,
            launchBehaviour = LaunchBehaviour.DropLatest,
            launchIn = DispatcherProvider.io,
            call = { getAppointmentHistory.loadMore() },
            onComplete = { uiState.appointments.append(mapToItems(it)) },
            onError = { uiState.notifications.add(it.notification()) }
        )
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
                onItemClick = weakVMClosure { it.onAppointmentClick(appointment) }
            )
        }
    }

    private fun AppointmentHistoryState.setup() = apply {
        appBar.size = TopBarSize.SMALL
        appBar.title = AppointmentsRes.strings.appointments_history_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(AppointmentHistoryDestinations.Back) }
        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakVMClosure { vm, value -> vm.onSearchQueryChanged(value) }
        refresh.onRefresh = weakVMClosure { it.onRefresh() }
        appointments.loadMore = weakVMClosure { it.onLoadMore() }
        appointments.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = AppointmentsRes.strings.appointments_history_empty.desc(),
        )
    }

    private companion object {
        const val LOAD_KEY = "appointment_history_load"
        const val SEARCH_DEBOUNCE_MS = 500L
    }
}
