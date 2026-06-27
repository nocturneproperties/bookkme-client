package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.UpdateAppointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.appointmentEvents
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
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
    fun `emits Updated event on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = stubAppointment()
        everySuspend { fixture.dataSource.updateAppointment(appointment) } returns appointment
        everySuspend { fixture.dataSource.saveAppointmentInDB(appointment) } returns Unit
        val events = mutableListOf<AppointmentEvent>()
        val job = launch(Dispatchers.Unconfined) { appointmentEvents.collect { events.add(it) } }

        whenn()
        fixture.sut(appointment)

        then()
        job.cancel()
        assertTrue(events.any { it is AppointmentEvent.Updated })
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
}
