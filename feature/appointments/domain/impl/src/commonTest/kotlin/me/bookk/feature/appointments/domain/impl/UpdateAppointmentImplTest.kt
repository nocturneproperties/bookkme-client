package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateAppointmentImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private class Fixture {
        val dataSource = mock<AppointmentDataSource>()
        val sut = UpdateAppointmentImpl(dataSource)
    }

    @Test
    fun `updates appointment via datasource and saves in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        val updated = appointment.copy(note = "updated")
        everySuspend { fixture.dataSource.updateAppointment(appointment) } returns updated
        everySuspend { fixture.dataSource.saveAppointmentInDB(updated) } returns Unit

        whenn()
        fixture.sut(appointment)

        then()
        verifySuspend { fixture.dataSource.updateAppointment(appointment) }
        verifySuspend { fixture.dataSource.saveAppointmentInDB(updated) }
    }

    @Test
    fun `throws DateIsNotAllowed on DATE_NOT_ALLOWED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.updateAppointment(appointment) } throws
            DomainError.BusinessError(AppointmentErrorCodes.DATE_NOT_ALLOWED, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointment.Error.DateIsNotAllowed> {
            fixture.sut(appointment)
        }
    }

    @Test
    fun `throws TimeIsNotAllowed on TIME_NOT_ALLOWED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.updateAppointment(appointment) } throws
            DomainError.BusinessError(AppointmentErrorCodes.TIME_NOT_ALLOWED, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointment.Error.TimeIsNotAllowed> {
            fixture.sut(appointment)
        }
    }

    @Test
    fun `throws AppointmentOverlap on APPOINTMENT_EXISTS error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.updateAppointment(appointment) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_EXISTS, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointment.Error.AppointmentOverlap> {
            fixture.sut(appointment)
        }
    }

    @Test
    fun `returns appointment stored by the backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        val stored = appointment.copy(note = "stored")
        everySuspend { fixture.dataSource.updateAppointment(appointment) } returns stored
        everySuspend { fixture.dataSource.saveAppointmentInDB(stored) } returns Unit

        whenn()
        val result = fixture.sut(appointment)

        then()
        assertEquals(stored, result)
    }

    @Test
    fun `throws AppointmentNotScheduled on APPOINTMENT_ALREADY_CANCELED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.updateAppointment(appointment) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_ALREADY_CANCELED, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointment.Error.AppointmentNotScheduled> {
            fixture.sut(appointment)
        }
    }

    @Test
    fun `throws AppointmentNotScheduled on APPOINTMENT_ALREADY_COMPLETED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.updateAppointment(appointment) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_ALREADY_COMPLETED, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointment.Error.AppointmentNotScheduled> {
            fixture.sut(appointment)
        }
    }

    @Test
    fun `throws AppointmentNotScheduled on APPOINTMENT_MARKED_NO_SHOW error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.updateAppointment(appointment) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_MARKED_NO_SHOW, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointment.Error.AppointmentNotScheduled> {
            fixture.sut(appointment)
        }
    }

    @Test
    fun `throws EmployeeSuspended on BUSINESS_EMPLOYEE_SUSPENDED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.updateAppointment(appointment) } throws
            DomainError.BusinessError(AppointmentErrorCodes.BUSINESS_EMPLOYEE_SUSPENDED, "msg")

        whenn()
        then()
        assertFailsWith<UpdateAppointment.Error.EmployeeSuspended> {
            fixture.sut(appointment)
        }
    }
}
