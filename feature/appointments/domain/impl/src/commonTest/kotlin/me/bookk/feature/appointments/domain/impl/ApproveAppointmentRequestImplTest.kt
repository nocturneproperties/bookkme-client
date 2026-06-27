package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.mock
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
import me.bookk.feature.appointments.domain.api.ApproveAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentEvent
import me.bookk.feature.appointments.domain.api.entity.appointmentEvents
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class ApproveAppointmentRequestImplTest {

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
        val dataSource = mock<AppointmentRequestDataSource>()
        val sut = ApproveAppointmentRequestImpl(dataSource)
    }

    @Test
    fun `returns appointment created from request`() = runUnitTest {
        given()
        val fixture = Fixture()
        val requestId = Uuid.random()
        val expected = stubAppointment()
        everySuspend { fixture.dataSource.createAppointmentFromRequest(requestId) } returns expected

        whenn()
        val result = fixture.sut(requestId)

        then()
        assertEquals(expected, result)
    }

    @Test
    fun `emits Created event on success`() = runUnitTest {
        given()
        val fixture = Fixture()
        val requestId = Uuid.random()
        everySuspend { fixture.dataSource.createAppointmentFromRequest(requestId) } returns stubAppointment()
        val events = mutableListOf<AppointmentEvent>()
        val job = launch(Dispatchers.Unconfined) { appointmentEvents.collect { events.add(it) } }

        whenn()
        fixture.sut(requestId)

        then()
        job.cancel()
        assertTrue(events.any { it is AppointmentEvent.Created })
    }

    @Test
    fun `throws AppointmentExists on APPOINTMENT_EXISTS error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val requestId = Uuid.random()
        everySuspend { fixture.dataSource.createAppointmentFromRequest(requestId) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_EXISTS, "msg")

        whenn()
        then()
        assertFailsWith<ApproveAppointmentRequest.Error.AppointmentExists> {
            fixture.sut(requestId)
        }
    }

    @Test
    fun `throws DateNotAllowed on DATE_NOT_ALLOWED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val requestId = Uuid.random()
        everySuspend { fixture.dataSource.createAppointmentFromRequest(requestId) } throws
            DomainError.BusinessError(AppointmentErrorCodes.DATE_NOT_ALLOWED, "msg")

        whenn()
        then()
        assertFailsWith<ApproveAppointmentRequest.Error.DateNotAllowed> {
            fixture.sut(requestId)
        }
    }

    @Test
    fun `throws TimeNotAllowed on TIME_NOT_ALLOWED error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val requestId = Uuid.random()
        everySuspend { fixture.dataSource.createAppointmentFromRequest(requestId) } throws
            DomainError.BusinessError(AppointmentErrorCodes.TIME_NOT_ALLOWED, "msg")

        whenn()
        then()
        assertFailsWith<ApproveAppointmentRequest.Error.TimeNotAllowed> {
            fixture.sut(requestId)
        }
    }

    @Test
    fun `throws DateInPast on DATE_IN_PAST error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val requestId = Uuid.random()
        everySuspend { fixture.dataSource.createAppointmentFromRequest(requestId) } throws
            DomainError.BusinessError(AppointmentErrorCodes.DATE_IN_PAST, "msg")

        whenn()
        then()
        assertFailsWith<ApproveAppointmentRequest.Error.DateInPast> {
            fixture.sut(requestId)
        }
    }
}
