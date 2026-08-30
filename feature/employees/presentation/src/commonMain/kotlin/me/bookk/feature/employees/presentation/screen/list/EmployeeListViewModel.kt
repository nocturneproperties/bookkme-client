package me.bookk.feature.employees.presentation.screen.list

import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.employees.resources.EmployeesRes
import me.bookk.core.capitalizeChar
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.employees.domain.api.GetEmployees
import me.bookk.feature.employees.presentation.EmployeesStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class EmployeeListViewModel(
    @InjectedParam private val businessId: Uuid,
    private val getEmployees: GetEmployees,
    stateFactory: EmployeesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: EmployeeListState = stateFactory.createEmployeeListState().setup()
    private var items = listOf<EmployeeSection>()

    init {
        loadEmployees()
    }

    private fun loadEmployees() {
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.refreshState.isRefreshing = true },
            call = { getEmployees(businessId) },
            onComplete = {
                val grouped = it
                    .sortedBy { employee -> employee.fullName.trim() }
                    .groupBy { employee -> employee.name[0].toString().capitalizeChar() }
                    .map { entry -> EmployeeSection(id = entry.key, header = entry.key, items = entry.value) }
                items = grouped
                uiState.employeesList.replace(grouped)
            },
            onError = { uiState.notifications.add(it.notification()) },
            onTerminate = {
                uiState.refreshState.isRefreshing = false
                uiState.employeesList.isInitialLoading = false
            }
        )
    }

    private fun onSearchQueryChanged(query: String) {
        uiState.searchField.text = query
        if (query.isBlank()) {
            uiState.employeesList.replace(items)
            return
        }
        val filtered = items
            .map { section ->
                section.copy(items = section.items.filter { it.fullName.contains(query, ignoreCase = true) })
            }
            .filter { it.items.isNotEmpty() }
        uiState.employeesList.replace(filtered)
    }

    private fun EmployeeListState.setup() = apply {
        appBar.title = EmployeesRes.strings.employees_title.desc()
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(EmployeeListDestinations.Back) }
        appBar.actions.replace(
            listOf(
                AppBarAction(
                    icon = DesignSystem.images.plus,
                    contentDescription = DesignSystem.strings.action_add.desc(),
                    onClick = weakVMClosure { it.uiState.navigation.push(EmployeeListDestinations.AddEmployee(businessId)) }
                )
            )
        )
        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakVMClosure { vm, value -> vm.onSearchQueryChanged(value) }
        refreshState.onRefresh = weakVMClosure { it.loadEmployees() }
        employeesList.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = EmployeesRes.strings.employees_empty.desc()
        )
    }
}
