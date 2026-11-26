package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import library.money.api.Money
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetClientAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestStatus
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.booking.domain.api.entity.BookingRequestDetails
import me.bookk.feature.booking.domain.api.entity.BookingSummary
import me.bookk.feature.booking.domain.api.entity.BookingSummary.BookedService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class GetBookingRequestDetailsImplTest {

    private val requestId = Uuid.random()
    private val startsAt = LocalDateTime(2026, 11, 27, 11, 0)
    private val manicure = serviceSnapshot("Manicure", 45.minutes, Money(55_000, UAH))
    private val polish = serviceSnapshot("Gel polish", 15.minutes, Money(20_000, UAH))

    private class Fixture {
        val getClientAppointmentRequest = mock<GetClientAppointmentRequest>()
        val sut = GetBookingRequestDetailsImpl(getClientAppointmentRequest)

        fun stubCache(id: Uuid, request: AppointmentRequest?) {
            every { getClientAppointmentRequest.flow(id) } returns flowOf(request)
        }
    }

    @Test
    fun `emits the request with its business, specialist, times, note and prices`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(requestId, cached(services = listOf(manicure, polish)))

        whenn()
        val details = fixture.sut.flow(requestId).first()

        then()
        assertEquals(
            BookingRequestDetails(
                id = requestId,
                summary = BookingSummary(
                    businessName = "Nail Room",
                    specialistName = "Maria Shevchuk",
                    startsAt = startsAt,
                    endsAt = LocalDateTime(2026, 11, 27, 12, 0),
                    note = "Short nails, please.",
                    services = listOf(
                        BookedService("Manicure", 1, Money(55_000, UAH)),
                        BookedService("Gel polish", 1, Money(20_000, UAH)),
                    ),
                    total = Money(75_000, UAH),
                ),
                status = BookingRequestDetails.Status.WAITING_FOR_CONFIRMATION,
            ),
            details,
        )
    }

    @Test
    fun `counts a service booked more than once at its combined price`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(requestId, cached(services = listOf(polish, polish, manicure)))

        whenn()
        val summary = fixture.sut.flow(requestId).first()?.summary

        then()
        assertEquals(
            listOf(
                BookedService("Gel polish", 2, Money(40_000, UAH)),
                BookedService("Manicure", 1, Money(55_000, UAH)),
            ),
            summary?.services,
        )
        assertEquals(Money(95_000, UAH), summary?.total)
    }

    @Test
    fun `emits nothing for a request without services`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(requestId, cached(services = emptyList()))

        whenn()
        val details = fixture.sut.flow(requestId).first()

        then()
        assertNull(details)
    }

    @Test
    fun `maps every request status`() = runUnitTest {
        given()
        val fixture = Fixture()
        val statuses = AppointmentRequestStatus.entries

        whenn()
        val mapped = statuses.map { status ->
            fixture.stubCache(requestId, cached(status = status))
            fixture.sut.flow(requestId).first()?.status
        }

        then()
        assertEquals(
            listOf(
                BookingRequestDetails.Status.WAITING_FOR_CONFIRMATION,
                BookingRequestDetails.Status.CONFIRMED,
                BookingRequestDetails.Status.DECLINED,
                BookingRequestDetails.Status.CLOSED,
            ),
            mapped,
        )
    }

    @Test
    fun `emits nothing while the request is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(requestId, null)

        whenn()
        val details = fixture.sut.flow(requestId).first()

        then()
        assertNull(details)
    }

    private fun cached(
        services: List<ServiceSnapshot> = listOf(manicure),
        status: AppointmentRequestStatus = AppointmentRequestStatus.PENDING,
    ): AppointmentRequest {
        val request = AppointmentRequest(
            id = requestId,
            userId = Uuid.random(),
            businessId = Uuid.random(),
            employee = EmployeeSnapshot(Uuid.random(), Uuid.random(), "Maria Shevchuk"),
            client = ClientSnapshot(Uuid.random(), "Olena", null, null),
            services = services,
            status = status,
            date = startsAt.toInstant(TimeZone.currentSystemDefault()),
            note = "Short nails, please.",
            declineReason = "",
        )
        return request.copy(businessName = "Nail Room")
    }

    private fun serviceSnapshot(name: String, duration: Duration, price: Money): ServiceSnapshot {
        return ServiceSnapshot(Uuid.random(), name, Uuid.random(), price, duration)
    }

    private companion object {
        val UAH = Money.SupportedCurrency.UAH
    }
}
