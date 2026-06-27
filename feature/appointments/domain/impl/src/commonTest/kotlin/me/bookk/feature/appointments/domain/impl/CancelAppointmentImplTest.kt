package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.matcher.matches
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
import me.bookk.feature.appointments.domain.api.CancelAppointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.appointmentEvents
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class CancelAppointmentImplTest {

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
        val sut = CancelAppointmentImpl(dataSource)
    }

    @Test
    fun `returns cancelled appointment`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointmentId = Uuid.random()
        val businessId = Uuid.random()
        val expected = stubAppointment(id = appointmentId)
        everySuspend { fixture.dataSource.cancelAppointment(any()) } returns expected

        whenn()
        val result = fixture.sut(appointmentId, businessId, "reason")

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `passes correct cancellation to datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointmentId = Uuid.random()
        val businessId = Uuid.random()
        val reason = "no longer needed"
        everySuspend { fixture.dataSource.cancelAppointment(any()) } returns stubAppointment()

        whenn()
        fixture.sut(appointmentId, businessId, reason)

        then()
        verifySuspend {
            fixture.dataSource.cancelAppointment(
                matches({ "match" }) { it.id == appointmentId && it.businessId == businessId && it.reason == reason }
            )
        }
    }

    @Test
    fun `emits Cancelled event on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.cancelAppointment(any()) } returns stubAppointment()
        val events = mutableListOf<AppointmentEvent>()
        val job = launch(Dispatchers.Unconfined) { appointmentEvents.collect { events.add(it) } }

        whenn()
        fixture.sut(Uuid.random(), Uuid.random(), "reason")

        then()
        job.cancel()
        assertTrue(events.any { it is AppointmentEvent.Cancelled })
    }

    @Test
    fun `throws AppointmentAlreadyCancelled on corresponding error code`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.cancelAppointment(any()) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_ALREADY_CANCELED, "msg")

        whenn()
        then()
        assertFailsWith<CancelAppointment.Error.AppointmentAlreadyCancelled> {
            fixture.sut(Uuid.random(), Uuid.random(), "reason")
        }
    }

    @Test
    fun `throws AppointmentAlreadyCompleted on corresponding error code`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.cancelAppointment(any()) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_ALREADY_COMPLETED, "msg")

        whenn()
        then()
        assertFailsWith<CancelAppointment.Error.AppointmentAlreadyCompleted> {
            fixture.sut(Uuid.random(), Uuid.random(), "reason")
        }
    }
}
