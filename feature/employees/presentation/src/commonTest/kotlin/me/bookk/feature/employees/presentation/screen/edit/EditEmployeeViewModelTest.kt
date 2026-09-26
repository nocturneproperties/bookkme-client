package me.bookk.feature.employees.presentation.screen.edit

import dev.icerock.moko.resources.desc.desc
import dev.mokkery.answering.calls
import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.capture.Capture
import dev.mokkery.matcher.capture.capture
import dev.mokkery.matcher.capture.get
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.MutableStateFlow
import library.device.api.DeviceFacade
import me.bookk.android.feature.employees.resources.EmployeesRes
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.test.FakeDateLocalizer
import me.bookk.designsystem.test.FakeErrorMapper
import me.bookk.designsystem.test.TestException
import me.bookk.designsystem.test.ViewModelTestDispatchers
import me.bookk.designsystem.test.assertMappedSingle
import me.bookk.designsystem.test.assertSingle
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.employees.domain.api.CanEditEmployees
import me.bookk.feature.employees.domain.api.GetAssignableServices
import me.bookk.feature.employees.domain.api.GetEmployee
import me.bookk.feature.employees.domain.api.IsBusinessOwner
import me.bookk.feature.employees.domain.api.UpdateEmployee
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.presentation.FakeEmployeesStateFactory
import me.bookk.feature.employees.presentation.stubEmployee
import me.bookk.feature.employees.presentation.stubPermissions
import me.bookk.feature.employees.presentation.stubService
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class EditEmployeeViewModelTest {

    private val dispatchers = ViewModelTestDispatchers()

    @BeforeTest
    fun setUp() {
        dispatchers.install()
    }

    @AfterTest
    fun tearDown() {
        dispatchers.uninstall()
    }

    private class Fixture(
        val employee: Employee = stubEmployee(),
        isOwner: Boolean = false,
        canEdit: Boolean = true,
        isCurrentUserOwner: Boolean = true
    ) {
        val services = MutableStateFlow<List<Service>>(emptyList())
        val getEmployee = mock<GetEmployee> {
            everySuspend { invoke(employee.id) } returns employee
        }
        val getAssignableServices = mock<GetAssignableServices> {
            every { flow() } returns services
            everySuspend { refresh(any()) } returns emptyList()
        }
        val isBusinessOwner = mock<IsBusinessOwner> {
            everySuspend { invoke(employee.userId, employee.businessId) } returns isOwner
            everySuspend { invoke(employee.businessId) } returns isCurrentUserOwner
        }
        val canEditEmployees = mock<CanEditEmployees> {
            everySuspend { invoke(employee.businessId) } returns canEdit
        }
        val updateEmployee = mock<UpdateEmployee>()
        val device = mock<DeviceFacade> {
            every { dial(any()) } returns Unit
            every { mail(any()) } returns Unit
        }
        val errorMapper = FakeErrorMapper()
        val updates = Capture.slot<Employee>()

        fun sut() = EditEmployeeViewModel(
            employeeId = employee.id,
            getEmployee = getEmployee,
            getAssignableServices = getAssignableServices,
            isBusinessOwner = isBusinessOwner,
            canEditEmployees = canEditEmployees,
            updateEmployee = updateEmployee,
            device = device,
            dateLocalizer = FakeDateLocalizer(),
            stateFactory = FakeEmployeesStateFactory(),
            vmArgs = VmArgs(errorMapper)
        )

        fun stubUpdateEcho() {
            everySuspend { updateEmployee(capture(updates)) } calls { (updated: Employee) -> updated }
        }
    }

    private fun EditEmployeeViewModel.permissionRow(index: Int): ResourcePermissionState {
        return uiState.permissions.items[index]
    }

    @Test
    fun `shows employee full name as title`() = runUnitTest {
        given()
        val fixture = Fixture(stubEmployee(name = "Jane", lastName = "Doe"))

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals("Jane Doe".desc(), sut.uiState.appBar.title)
    }

    @Test
    fun `renders phone and email as info lines`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(
            listOf(fixture.employee.phone!!.desc(), fixture.employee.email!!.desc()),
            sut.uiState.contacts.items.map { it.value }
        )
    }

    @Test
    fun `dials the employee phone on phone line click`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()

        whenn()
        sut.uiState.contacts.items.first().onClick?.invoke()

        then()
        verify { fixture.device.dial(fixture.employee.phone!!) }
    }

    @Test
    fun `selects the services the employee provides`() = runUnitTest {
        given()
        val service = stubService()
        val fixture = Fixture(stubEmployee(services = listOf(service)))

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(listOf(service), sut.uiState.services.selectedItems.map { it.service })
    }

    @Test
    fun `shows a placeholder while the employee provides no services`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(EmployeesRes.strings.employees_edit_services_empty.desc(), sut.uiState.services.placeholder)
    }

    @Test
    fun `offers the business services as options`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        val services = listOf(stubService("Haircut"), stubService("Coloring"))

        whenn()
        fixture.services.value = services

        then()
        assertEquals(services, sut.uiState.services.options.map { it.service })
    }

    @Test
    fun `refreshes the services of the employee business`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        fixture.sut()

        then()
        verifySuspend { fixture.getAssignableServices.refresh(fixture.employee.businessId) }
    }

    @Test
    fun `renders a permission row per business resource with current grants`() = runUnitTest {
        given()
        val fixture = Fixture(
            stubEmployee(permissions = stubPermissions(clients = ResourcePermission(view = true, update = true)))
        )

        whenn()
        val sut = fixture.sut()

        then()
        assertEquals(5, sut.uiState.permissions.items.size)
        val clients = sut.permissionRow(2)
        assertEquals(listOf(true, true, false), listOf(clients.canView.isChecked, clients.canUpdate.isChecked, clients.canDelete.isChecked))
    }

    @Test
    fun `shows permissions without a hint for a regular employee`() = runUnitTest {
        given()
        val fixture = Fixture(isOwner = false)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.isPermissionsVisible)
        assertNull(sut.uiState.permissionsHint)
    }

    @Test
    fun `shows only the hint to a non-owner who can edit employees`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = true, isCurrentUserOwner = false)

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.isPermissionsVisible)
        assertEquals(EmployeesRes.strings.employees_edit_permissions_owner_hint.desc(), sut.uiState.permissionsHint)
    }

    @Test
    fun `shows only the hint to a non-owner who cannot edit employees`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = false, isCurrentUserOwner = false)

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.isPermissionsVisible)
        assertEquals(EmployeesRes.strings.employees_edit_permissions_owner_hint.desc(), sut.uiState.permissionsHint)
    }

    @Test
    fun `shows only the hint to a non-owner viewing the business owner`() = runUnitTest {
        given()
        val fixture = Fixture(isOwner = true, isCurrentUserOwner = false)

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.isPermissionsVisible)
        assertEquals(EmployeesRes.strings.employees_edit_permissions_owner_hint.desc(), sut.uiState.permissionsHint)
    }

    @Test
    fun `shows save for a user who can edit employees`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = true)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.save.isVisible)
    }

    @Test
    fun `hides save for a user who cannot edit employees`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = false)

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.save.isVisible)
    }

    @Test
    fun `hides save while the employee is loading`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getEmployee(fixture.employee.id) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.save.isVisible)
    }

    @Test
    fun `keeps services and schedule editable for a user who can edit employees`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = true)

        whenn()
        val sut = fixture.sut()

        then()
        assertTrue(sut.uiState.services.isEditable)
        assertTrue(sut.uiState.schedule.isEditable)
        assertTrue(sut.uiState.schedule.dayOffs.isEditable)
        assertTrue(sut.uiState.schedule.monday.isActive.isEnabled)
    }

    @Test
    fun `makes services and schedule read-only for a user who cannot edit employees`() = runUnitTest {
        given()
        val fixture = Fixture(canEdit = false)

        whenn()
        val sut = fixture.sut()

        then()
        val monday = sut.uiState.schedule.monday
        assertFalse(sut.uiState.services.isEditable)
        assertFalse(sut.uiState.schedule.isEditable)
        assertFalse(sut.uiState.schedule.dayOffs.isEditable)
        assertFalse(monday.isActive.isEnabled)
        assertTrue(monday.intervals.isNotEmpty())
        assertTrue(monday.intervals.none { it.timeFromPicker.textField.enabled || it.timeToPicker.textField.enabled })
    }

    @Test
    fun `hides permissions and explains why for the business owner`() = runUnitTest {
        given()
        val fixture = Fixture(isOwner = true)

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.isPermissionsVisible)
        assertEquals(EmployeesRes.strings.employees_edit_permissions_owner_hint.desc(), sut.uiState.permissionsHint)
    }

    @Test
    fun `keeps permissions hidden and hint empty while the employee is loading`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getEmployee(fixture.employee.id) } throws TestException()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.isPermissionsVisible)
        assertNull(sut.uiState.permissionsHint)
    }

    @Test
    fun `keeps save disabled while nothing changed`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val sut = fixture.sut()

        then()
        assertFalse(sut.uiState.save.isEnabled)
    }

    @Test
    fun `enables save when a permission is toggled`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.permissionRow(1).canUpdate.onCheckedChange?.invoke(true)

        then()
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `disables save when a toggled permission is toggled back`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        sut.permissionRow(1).canUpdate.onCheckedChange?.invoke(true)

        whenn()
        sut.permissionRow(1).canUpdate.onCheckedChange?.invoke(false)

        then()
        assertFalse(sut.uiState.save.isEnabled)
    }

    @Test
    fun `enables save when a service is added`() = runUnitTest {
        given()
        val fixture = Fixture()
        val sut = fixture.sut()
        val service = EmployeeServicePresentation(stubService())

        whenn()
        sut.uiState.services.onItemsPicked(listOf(service))

        then()
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `enables save when a service is removed`() = runUnitTest {
        given()
        val fixture = Fixture(stubEmployee(services = listOf(stubService())))
        val sut = fixture.sut()

        whenn()
        sut.uiState.services.onItemsRemoveRequested(sut.uiState.services.selectedItems.toList())

        then()
        assertTrue(sut.uiState.services.selectedItems.isEmpty())
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `enables save when a schedule day is toggled`() = runUnitTest {
        given()
        val sut = Fixture().sut()
        val monday = sut.uiState.schedule.monday

        whenn()
        monday.isActive.onCheckedChange?.invoke(!monday.isActive.isChecked)

        then()
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `saves edited services, schedule and permissions`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubUpdateEcho()
        val sut = fixture.sut()
        val service = stubService()
        sut.uiState.services.onItemsPicked(listOf(EmployeeServicePresentation(service)))
        sut.uiState.schedule.monday.isActive.onCheckedChange?.invoke(false)
        sut.permissionRow(4).canView.onCheckedChange?.invoke(true)

        whenn()
        sut.uiState.save.onClick?.invoke()

        then()
        val saved = fixture.updates.get()
        assertEquals(listOf(service), saved.services)
        assertFalse(saved.schedule.monday.isActive)
        assertEquals(ResourcePermission(view = true), saved.permissions.appointments)
        assertEquals(fixture.employee.id, saved.id)
    }

    @Test
    fun `shows success message and disables save after saving`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubUpdateEcho()
        val sut = fixture.sut()
        sut.permissionRow(0).canView.onCheckedChange?.invoke(true)

        whenn()
        sut.uiState.save.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.GlobalMessage>()
        assertFalse(sut.uiState.save.isEnabled)
        assertFalse(sut.uiState.save.isLoading)
    }

    @Test
    fun `shows message when a permission cannot be granted`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateEmployee(any()) } throws UpdateEmployee.Error.InsufficientGrant(TestException())
        val sut = fixture.sut()
        sut.permissionRow(0).canDelete.onCheckedChange?.invoke(true)

        whenn()
        sut.uiState.save.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
        assertTrue(sut.uiState.save.isEnabled)
    }

    @Test
    fun `shows message when an active day has no work hours`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateEmployee(any()) } throws UpdateEmployee.Error.ActiveDayWithoutWorkHours(TestException())
        val sut = fixture.sut()
        sut.permissionRow(0).canView.onCheckedChange?.invoke(true)

        whenn()
        sut.uiState.save.onClick?.invoke()

        then()
        sut.uiState.notifications.assertSingle<PresentationNotification.Message>()
        assertTrue(fixture.errorMapper.mappedErrors.isEmpty())
    }

    @Test
    fun `shows mapped error when save fails unexpectedly`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.updateEmployee(any()) } throws TestException()
        val sut = fixture.sut()
        sut.permissionRow(0).canView.onCheckedChange?.invoke(true)

        whenn()
        sut.uiState.save.onClick?.invoke()

        then()
        fixture.errorMapper.assertMappedSingle(TestException::class)
    }

    @Test
    fun `shows mapped error when the employee cannot be loaded`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getEmployee(fixture.employee.id) } throws GetEmployee.Error.NotFound()

        whenn()
        fixture.sut()

        then()
        fixture.errorMapper.assertMappedSingle(GetEmployee.Error.NotFound::class)
    }

    @Test
    fun `pushes back destination on back click`() = runUnitTest {
        given()
        val sut = Fixture().sut()

        whenn()
        sut.uiState.appBar.onBackClick?.invoke()

        then()
        assertEquals(listOf<EditEmployeeDestinations>(EditEmployeeDestinations.Back), sut.uiState.navigation.navigationDestination)
    }
}
