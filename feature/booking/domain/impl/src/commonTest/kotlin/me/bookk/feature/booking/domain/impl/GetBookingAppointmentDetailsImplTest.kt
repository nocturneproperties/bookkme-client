package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import library.money.api.Money
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetClientAppointment
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.ClientSnapshot
import me.bookk.feature.appointments.domain.api.entity.EmployeeSnapshot
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustment
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import me.bookk.feature.booking.domain.api.entity.BookingAppointmentDetails
import me.bookk.feature.booking.domain.api.entity.BookingSummary
import me.bookk.feature.booking.domain.api.entity.BookingSummary.BookedService
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class GetBookingAppointmentDetailsImplTest {

    private val appointmentId = Uuid.random()
    private val startsAt = LocalDateTime(2026, 11, 24, 14, 30)
    private val haircut = ServiceSnapshot(
        Uuid.random(),
        "Haircut",
        Uuid.random(),
        Money(60_000, Money.SupportedCurrency.UAH),
        65.minutes,
    )

    private class Fixture {
        val getClientAppointment = mock<GetClientAppointment>()
        val sut = GetBookingAppointmentDetailsImpl(getClientAppointment)

        fun stubCache(id: Uuid, appointment: Appointment?) {
            every { getClientAppointment.flow(id) } returns flowOf(appointment)
        }
    }

    @Test
    fun `emits the appointment with the client's note, not the business's`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(appointmentId, cached())

        whenn()
        val details = fixture.sut.flow(appointmentId).first()

        then()
        assertEquals(
            BookingAppointmentDetails(
                id = appointmentId,
                summary = BookingSummary(
                    businessName = "Studio Lumen",
                    specialistName = "Anna Kovalenko",
                    startsAt = startsAt,
                    endsAt = LocalDateTime(2026, 11, 24, 15, 35),
                    note = "Short at the back.",
                    services = listOf(
                        BookedService("Haircut", 1, Money(60_000, Money.SupportedCurrency.UAH)),
                    ),
                    total = Money(60_000, Money.SupportedCurrency.UAH),
                ),
                status = BookingAppointmentDetails.Status.CONFIRMED,
            ),
            details,
        )
    }

    @Test
    fun `shows the provided services and the charged price of an adjusted appointment`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val beard = ServiceSnapshot(
                Uuid.random(),
                "Beard trim",
                Uuid.random(),
                Money(20_000, Money.SupportedCurrency.UAH),
                10.minutes,
            )
            val adjusted = cached(AppointmentStatus.COMPLETED).copy(
                priceAdjustment = PriceAdjustment(
                    additionalServices = listOf(haircut, beard),
                    price = Money(70_000, Money.SupportedCurrency.UAH),
                    reason = "Added a beard trim",
                ),
            )
            fixture.stubCache(appointmentId, adjusted)

            whenn()
            val summary = fixture.sut.flow(appointmentId).first()?.summary

            then()
            assertEquals(listOf("Haircut", "Beard trim"), summary?.services?.map { it.name })
            assertEquals(Money(70_000, Money.SupportedCurrency.UAH), summary?.total)
            assertEquals(LocalDateTime(2026, 11, 24, 15, 35), summary?.endsAt)
        }

    @Test
    fun `emits nothing for an appointment without services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val empty = cached().let {
            it.copy(services = emptyList())
        }
        fixture.stubCache(appointmentId, empty)

        whenn()
        val details = fixture.sut.flow(appointmentId).first()

        then()
        assertNull(details)
    }

    @Test
    fun `maps every appointment status`() = runUnitTest {
        given()
        val fixture = Fixture()
        val statuses = AppointmentStatus.entries

        whenn()
        val mapped = statuses.map { status ->
            fixture.stubCache(appointmentId, cached(status))
            fixture.sut.flow(appointmentId).first()?.status
        }

        then()
        assertEquals(
            listOf(
                BookingAppointmentDetails.Status.CONFIRMED,
                BookingAppointmentDetails.Status.COMPLETED,
                BookingAppointmentDetails.Status.CANCELLED,
                BookingAppointmentDetails.Status.MISSED,
                BookingAppointmentDetails.Status.COMPLETED,
            ),
            mapped,
        )
    }

    @Test
    fun `emits nothing while the appointment is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(appointmentId, null)

        whenn()
        val details = fixture.sut.flow(appointmentId).first()

        then()
        assertNull(details)
    }

    private fun cached(status: AppointmentStatus = AppointmentStatus.SCHEDULED): Appointment {
        val appointment = Appointment(
            id = appointmentId,
            userId = Uuid.random(),
            businessId = Uuid.random(),
            employee = EmployeeSnapshot(Uuid.random(), Uuid.random(), "Anna Kovalenko"),
            client = ClientSnapshot(Uuid.random(), "Olena", null, null),
            services = listOf(haircut),
            status = status,
            date = startsAt,
            note = "",
            cancellationReason = "",
            clientNote = "Short at the back.",
        )
        return appointment.copy(businessName = "Studio Lumen")
    }
}
