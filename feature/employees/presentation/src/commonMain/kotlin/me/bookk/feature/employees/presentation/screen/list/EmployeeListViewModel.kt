package me.bookk.feature.employees.presentation.screen.list

import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import me.bookk.android.feature.employees.resources.EmployeesRes
import me.bookk.core.capitalizeChar
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.convenience.loadList
import me.bookk.designsystem.convenience.resetListOnChange
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.AppBarAction
import me.bookk.designsystem.uistate.simple.EmptyState
import me.bookk.feature.employees.domain.api.GetEmployees
import me.bookk.feature.employees.domain.api.ObserveCurrentBusinessId
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.presentation.EmployeesStateFactory
import kotlin.uuid.Uuid

class EmployeeListViewModel(
    private val getEmployees: GetEmployees,
    private val observeCurrentBusinessId: ObserveCurrentBusinessId,
    stateFactory: EmployeesStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: EmployeeListState = stateFactory.createEmployeeListState().setup()
    private var items = listOf<EmployeeSection>()

    init {
        observeEmployees()
        observeBusinessChanges()
    }

    private fun observeEmployees() {
        getEmployees.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { renderEmployees(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun renderEmployees(employees: List<Employee>) {
        if (employees.isEmpty() && uiState.employeesList.isInitialLoading) return
        val grouped = employees
            .sortedBy { it.fullName.trim() }
            .groupBy { it.name[0].toString().capitalizeChar() }
            .map { entry ->
                EmployeeSection(
                    id = entry.key,
                    header = entry.key,
                    items = entry.value,
                    onItemClick = weakVMClosure { vm, employee -> vm.onEmployeeClick(employee) }
                )
            }
        items = grouped
        uiState.employeesList.replace(grouped)
    }

    private fun observeBusinessChanges() {
        observeCurrentBusinessId()
            .filterNotNull()
            .flowOn(DispatcherProvider.io)
            .resetListOnChange(uiState.employeesList)
            .safeOnEach { loadEmployees(it) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadEmployees(businessId: Uuid) {
        loadList(
            listState = uiState.employeesList,
            notifications = uiState.notifications,
            refreshState = uiState.refreshState,
            call = { getEmployees.refresh(businessId) }
        )
    }

    private fun onAddEmployeeClick() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { observeCurrentBusinessId().filterNotNull().first() },
            onComplete = { uiState.navigation.push(EmployeeListDestinations.AddEmployee(it)) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun onEmployeeClick(employee: Employee) {
        uiState.navigation.push(EmployeeListDestinations.EditEmployee(employee.id))
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
                    onClick = weakVMClosure { it.onAddEmployeeClick() }
                )
            )
        )
        searchField.placeholder = DesignSystem.strings.action_search.desc()
        searchField.onTextChanged = weakVMClosure { vm, value -> vm.onSearchQueryChanged(value) }
        employeesList.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = EmployeesRes.strings.employees_empty.desc()
        )
    }
}
