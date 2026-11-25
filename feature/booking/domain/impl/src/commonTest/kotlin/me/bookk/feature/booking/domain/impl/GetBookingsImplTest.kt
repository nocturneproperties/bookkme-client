package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import library.money.api.Money
import me.bookk.core.domain.pagination.Page
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetClientBookings
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ClientBooking
import me.bookk.feature.appointments.domain.api.entity.ClientBookingPeriod
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingOverview
import me.bookk.feature.booking.domain.api.entity.BookingPeriod
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails
import me.bookk.feature.booking.domain.api.entity.BookingSummary
import me.bookk.feature.booking.domain.api.entity.BookingSummary.BookedService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class GetBookingsImplTest {

    private val startsAt = LocalDateTime(2026, 11, 24, 14, 30)
    private val haircutPrice = Money(60_000, Money.SupportedCurrency.UAH)
    private val haircut =
        ServiceSnapshot(Uuid.random(), "Haircut", Uuid.random(), haircutPrice, 65.minutes)

    private class Fixture {
        val getClientBookings = mock<GetClientBookings>()
        val sut = GetBookingsImpl(getClientBookings)
    }

    private fun appointment(
        status: AppointmentStatus = AppointmentStatus.SCHEDULED,
        services: List<ServiceSnapshot> = listOf(haircut),
    ): ClientBooking.Visit {
        val appointment = Appointment(
            id = Uuid.random(),
            userId = Uuid.random(),
            businessId = Uuid.random(),
            employee = EmployeeSnapshot(Uuid.random(), Uuid.random(), "Anna Kovalenko"),
            client = ClientSnapshot(Uuid.random(), "Olena", null, null),
            services = services,
            status = status,
            date = startsAt,
            note = "",
            cancellationReason = "",
        )
        return ClientBooking.Visit(appointment.copy(businessName = "Studio Lumen"))
    }

    private fun request(
        status: AppointmentRequestStatus = AppointmentRequestStatus.PENDING,
    ): ClientBooking.Request {
        val request = AppointmentRequest(
            id = Uuid.random(),
            userId = Uuid.random(),
            businessId = Uuid.random(),
            employee = EmployeeSnapshot(Uuid.random(), Uuid.random(), "Anna Kovalenko"),
            client = ClientSnapshot(Uuid.random(), "Olena", null, null),
            services = listOf(haircut),
            status = status,
            date = startsAt.toInstant(TimeZone.currentSystemDefault()),
            note = "",
            declineReason = "",
        )
        return ClientBooking.Request(request.copy(businessName = "Nail Room"))
    }

    @Test
    fun `flow sums up each listed booking`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = appointment()
        every {
            fixture.getClientBookings.flow(ClientBookingPeriod.UPCOMING)
        } returns flowOf(listOf(appointment))

        whenn()
        val result = fixture.sut.flow(BookingPeriod.UPCOMING).first()

        then()
        assertEquals(
            listOf(
                BookingOverview(
                    id = appointment.appointment.id,
                    summary = BookingSummary(
                        businessName = "Studio Lumen",
                        specialistName = "Anna Kovalenko",
                        startsAt = startsAt,
                        endsAt = LocalDateTime(2026, 11, 24, 15, 35),
                        note = "",
                        services = listOf(BookedService("Haircut", 1, haircutPrice)),
                        total = haircutPrice,
                    ),
                    status = BookingOverview.Status.Appointment(
                        BookingAppointmentDetails.Status.CONFIRMED,
                    ),
                ),
            ),
            result,
        )
    }

    @Test
    fun `flow keeps the order and tells requests from appointments`() = runUnitTest {
        given()
        val fixture = Fixture()
        val declined = request(AppointmentRequestStatus.DECLINED)
        val missed = appointment(AppointmentStatus.NO_SHOW)
        every {
            fixture.getClientBookings.flow(ClientBookingPeriod.PAST)
        } returns flowOf(listOf(declined, missed))

        whenn()
        val result = fixture.sut.flow(BookingPeriod.PAST).first()

        then()
        assertEquals(
            listOf(
                BookingOverview.Status.Request(BookingRequestDetails.Status.DECLINED),
                BookingOverview.Status.Appointment(BookingAppointmentDetails.Status.MISSED),
            ),
            result.map { it.status },
        )
    }

    @Test
    fun `flow leaves out bookings without services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val kept = request()
        every {
            fixture.getClientBookings.flow(ClientBookingPeriod.UPCOMING)
        } returns flowOf(listOf(appointment(services = emptyList()), kept))

        whenn()
        val result = fixture.sut.flow(BookingPeriod.UPCOMING).first()

        then()
        assertEquals(listOf(kept.request.id), result.map { it.id })
    }

    @Test
    fun `refresh returns the first page of the period with its next cursor`() = runUnitTest {
        given()
        val fixture = Fixture()
        val request = request()
        everySuspend {
            fixture.getClientBookings.refresh(ClientBookingPeriod.PAST)
        } returns Page(listOf(request), "next")

        whenn()
        val result = fixture.sut.refresh(BookingPeriod.PAST)

        then()
        assertEquals(listOf(request.request.id), result.items.map { it.id })
        assertEquals("next", result.nextCursor)
    }

    @Test
    fun `load more passes the cursor on`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointment = appointment()
        everySuspend {
            fixture.getClientBookings.loadMore(ClientBookingPeriod.UPCOMING, "next")
        } returns Page(listOf(appointment), null)

        whenn()
        val result = fixture.sut.loadMore(BookingPeriod.UPCOMING, "next")

        then()
        assertEquals(Page(listOf(appointment.appointment.id), null), result.map { it.id })
    }

    private fun <T, R> Page<T>.map(transform: (T) -> R): Page<R> {
        return Page(items.map(transform), nextCursor)
    }
}
