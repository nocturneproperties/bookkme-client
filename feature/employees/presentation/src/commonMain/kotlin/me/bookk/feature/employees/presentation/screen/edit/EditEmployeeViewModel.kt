package me.bookk.feature.employees.presentation.screen.edit

import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.flowOn
import library.device.api.DeviceFacade
import me.bookk.android.feature.employees.resources.EmployeesRes
import me.bookk.core.coroutine.DispatcherProvider
import me.bookk.core.dashOnBlank
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.date.DateLocalizer
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.presentation.error.PresentationNotification.GlobalMessage
import me.bookk.core.presentation.memory.weakVMClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.simple
import me.bookk.designsystem.uistate.schedule.DayOffPeriod
import me.bookk.designsystem.uistate.schedule.ScheduleBinder
import me.bookk.designsystem.uistate.schedule.WeekSchedule
import me.bookk.designsystem.uistate.schedule.WeekdaySchedule
import me.bookk.designsystem.uistate.schedule.WorkingHours
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.designsystem.uistate.startLoading
import me.bookk.designsystem.uistate.stopLoading
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.BusinessResource
import me.bookk.feature.business.domain.api.entity.DayOfWeekSchedule
import me.bookk.feature.business.domain.api.entity.DayOffRange
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkHour
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.employees.domain.api.GetAssignableServices
import me.bookk.feature.employees.domain.api.GetEmployee
import me.bookk.feature.employees.domain.api.UpdateEmployee
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.presentation.EmployeesStateFactory
import org.koin.core.annotation.InjectedParam
import kotlin.uuid.Uuid

class EditEmployeeViewModel(
    @InjectedParam private val employeeId: Uuid,
    private val getEmployee: GetEmployee,
    private val getAssignableServices: GetAssignableServices,
    private val updateEmployee: UpdateEmployee,
    private val device: DeviceFacade,
    private val stateFactory: EmployeesStateFactory,
    dateLocalizer: DateLocalizer,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    val uiState: EditEmployeeState = stateFactory.createEditEmployeeState().setup()
    private val scheduleBinder = ScheduleBinder(uiState.schedule, dateLocalizer, weakVMClosure { it.invalidateSaveState() })
    private val permissionStates: Map<BusinessResource, ResourcePermissionState> = createPermissionStates()
    private var referenceEmployee: Employee? = null

    init {
        uiState.permissions.replace(permissionStates.values.toList())
        observeServices()
        loadEmployee()
    }

    private fun EditEmployeeState.setup() = apply {
        appBar.onBackClick = weakVMClosure { it.uiState.navigation.push(EditEmployeeDestinations.Back) }
        save.text = DesignSystem.strings.action_save.desc()
        save.isEnabled = false
        save.onClick = weakVMClosure { it.onSaveClick() }
        services.pickerTitle = EmployeesRes.strings.employees_edit_services.desc()
        services.addItemText = EmployeesRes.strings.employees_edit_services_add.desc()
        services.onItemsPicked = weakVMClosure { vm, items -> vm.onServicesPicked(items) }
        services.onItemsRemoveRequested = weakVMClosure { vm, items -> vm.onServicesRemoved(items) }
    }

    private fun createPermissionStates(): Map<BusinessResource, ResourcePermissionState> {
        return BusinessResource.entries.associateWith { resource ->
            stateFactory.createResourcePermissionState().apply {
                id = resource.name
                title = resource.title().desc()
                canView.text = EmployeesRes.strings.employees_edit_permission_view.desc()
                canUpdate.text = EmployeesRes.strings.employees_edit_permission_update.desc()
                canDelete.text = EmployeesRes.strings.employees_edit_permission_delete.desc()
                canView.onCheckedChange = weakVMClosure { vm, isChecked -> vm.onPermissionChanged(resource) { it.canView.isChecked = isChecked } }
                canUpdate.onCheckedChange = weakVMClosure { vm, isChecked -> vm.onPermissionChanged(resource) { it.canUpdate.isChecked = isChecked } }
                canDelete.onCheckedChange = weakVMClosure { vm, isChecked -> vm.onPermissionChanged(resource) { it.canDelete.isChecked = isChecked } }
            }
        }
    }

    private fun observeServices() {
        getAssignableServices.flow()
            .flowOn(DispatcherProvider.io)
            .safeOnEach { services -> uiState.services.replaceOptions(services.map(::EmployeeServicePresentation)) }
            .onError { uiState.notifications.add(it.notification()) }
            .observe()
    }

    private fun loadEmployee() {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getEmployee(employeeId) },
            onComplete = {
                renderEmployee(it)
                refreshServices(it.businessId)
            },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun refreshServices(businessId: Uuid) {
        launch(
            launchIn = DispatcherProvider.io,
            call = { getAssignableServices.refresh(businessId) },
            onError = { uiState.notifications.add(it.notification()) }
        )
    }

    private fun renderEmployee(employee: Employee) {
        referenceEmployee = employee
        uiState.appBar.title = employee.fullName.desc()
        uiState.contacts.replace(
            listOf(
                InfoLine(
                    title = EmployeesRes.strings.employees_edit_phone,
                    value = employee.phone.dashOnBlank(),
                    onClick = weakVMClosure { vm -> employee.phone?.let { vm.device.dial(it) } }
                ),
                InfoLine(
                    title = EmployeesRes.strings.employees_edit_email,
                    value = employee.email.dashOnBlank(),
                    onClick = weakVMClosure { vm -> employee.email?.let { vm.device.mail(it) } }
                )
            )
        )
        uiState.services.replaceSelected(employee.services.map(::EmployeeServicePresentation))
        scheduleBinder.render(employee.schedule.toWeekSchedule())
        permissionStates.forEach { (resource, state) -> state.render(employee.permissions.of(resource)) }
        invalidateSaveState()
    }

    private fun ResourcePermissionState.render(permission: ResourcePermission) {
        canView.isChecked = permission.view
        canUpdate.isChecked = permission.update
        canDelete.isChecked = permission.delete
    }

    private fun onServicesPicked(items: List<EmployeeServicePresentation>) {
        val selectedIds = uiState.services.selectedItems.map { it.pickerItemId }.toSet()
        uiState.services.replaceSelected(uiState.services.selectedItems + items.filterNot { it.pickerItemId in selectedIds })
        invalidateSaveState()
    }

    private fun onServicesRemoved(items: List<EmployeeServicePresentation>) {
        val removedIds = items.map { it.pickerItemId }.toSet()
        uiState.services.replaceSelected(uiState.services.selectedItems.filterNot { it.pickerItemId in removedIds })
        invalidateSaveState()
    }

    private fun onPermissionChanged(resource: BusinessResource, change: (ResourcePermissionState) -> Unit) {
        change(permissionStates.getValue(resource))
        invalidateSaveState()
    }

    private fun onSaveClick() {
        val employee = snapshotEmployee() ?: return
        launch(
            launchIn = DispatcherProvider.io,
            onStart = { uiState.save.startLoading() },
            call = { updateEmployee(employee) },
            onComplete = {
                renderEmployee(it)
                uiState.notifications.add(GlobalMessage(EmployeesRes.strings.employees_edit_updated.desc()))
            },
            onError = { uiState.notifications.add(it.saveErrorNotification()) },
            onTerminate = {
                uiState.save.stopLoading()
                invalidateSaveState()
            }
        )
    }

    private fun Throwable.saveErrorNotification(): PresentationNotification {
        val message = when (this) {
            is UpdateEmployee.Error.ValidationError -> EmployeesRes.strings.employees_edit_error_validation
            is UpdateEmployee.Error.ActiveDayWithoutWorkHours -> EmployeesRes.strings.employees_edit_error_active_day_without_work_hours
            is UpdateEmployee.Error.InvalidDayOffRange -> EmployeesRes.strings.employees_edit_error_invalid_day_off_range
            is UpdateEmployee.Error.InsufficientGrant -> EmployeesRes.strings.employees_edit_error_insufficient_grant
            else -> return notification()
        }
        return PresentationNotification.Message.simple(message.desc())
    }

    private fun snapshotEmployee(): Employee? {
        return referenceEmployee?.copy(
            services = uiState.services.selectedItems.map { it.service },
            schedule = scheduleBinder.snapshot().toWorkingSchedule(),
            permissions = snapshotPermissions()
        )
    }

    private fun snapshotPermissions(): BusinessPermissions {
        return BusinessPermissions(
            business = permissionStates.getValue(BusinessResource.BUSINESS).toDomain(),
            employees = permissionStates.getValue(BusinessResource.EMPLOYEES).toDomain(),
            clients = permissionStates.getValue(BusinessResource.CLIENTS).toDomain(),
            services = permissionStates.getValue(BusinessResource.SERVICES).toDomain(),
            appointments = permissionStates.getValue(BusinessResource.APPOINTMENTS).toDomain()
        )
    }

    private fun ResourcePermissionState.toDomain(): ResourcePermission {
        return ResourcePermission(view = canView.isChecked, update = canUpdate.isChecked, delete = canDelete.isChecked)
    }

    private fun invalidateSaveState() {
        val reference = referenceEmployee
        uiState.save.isEnabled = reference != null && isChanged(reference)
    }

    private fun isChanged(reference: Employee): Boolean {
        return uiState.services.selectedItems.map { it.service.id }.toSet() != reference.services.map { it.id }.toSet() ||
            scheduleBinder.snapshot() != reference.schedule.toWeekSchedule() ||
            snapshotPermissions() != reference.permissions
    }

    private fun BusinessPermissions.of(resource: BusinessResource): ResourcePermission {
        return when (resource) {
            BusinessResource.BUSINESS -> business
            BusinessResource.EMPLOYEES -> employees
            BusinessResource.CLIENTS -> clients
            BusinessResource.SERVICES -> services
            BusinessResource.APPOINTMENTS -> appointments
        }
    }

    private fun BusinessResource.title(): StringResource {
        return when (this) {
            BusinessResource.BUSINESS -> EmployeesRes.strings.employees_edit_permission_business
            BusinessResource.EMPLOYEES -> EmployeesRes.strings.employees_edit_permission_employees
            BusinessResource.CLIENTS -> EmployeesRes.strings.employees_edit_permission_clients
            BusinessResource.SERVICES -> EmployeesRes.strings.employees_edit_permission_services
            BusinessResource.APPOINTMENTS -> EmployeesRes.strings.employees_edit_permission_appointments
        }
    }

    private fun WorkingSchedule.toWeekSchedule(): WeekSchedule {
        return WeekSchedule(
            days = listOf(monday, tuesday, wednesday, thursday, friday, saturday, sunday).map { day ->
                WeekdaySchedule(
                    dayOfWeek = day.dayOfWeek,
                    isActive = day.isActive,
                    workingHours = day.workingTime.map { WorkingHours(it.from, it.to) }
                )
            },
            dayOffs = dayOffs.map { DayOffPeriod(it.start, it.end) }
        )
    }

    private fun WeekSchedule.toWorkingSchedule(): WorkingSchedule {
        return WorkingSchedule(
            days = days.associate { day ->
                day.dayOfWeek to DayOfWeekSchedule(
                    dayOfWeek = day.dayOfWeek,
                    workingTime = day.workingHours.map { WorkHour(it.from, it.to) },
                    isActive = day.isActive
                )
            },
            dayOffs = dayOffs.map { DayOffRange(it.start, it.end) }
        )
    }
}
