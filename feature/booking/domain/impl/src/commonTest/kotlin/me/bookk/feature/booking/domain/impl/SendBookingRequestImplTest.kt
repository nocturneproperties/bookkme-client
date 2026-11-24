package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import library.money.api.Money
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.CreateAppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestDraft
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.booking.domain.api.SendBookingRequest.Error
import me.bookk.feature.booking.domain.datasource.BookingSelection
import me.bookk.feature.services.domain.api.quote.CreateQuote
import me.bookk.feature.services.domain.api.quote.entity.Quote
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlin.uuid.Uuid

class SendBookingRequestImplTest {

    private val businessId = Uuid.random()
    private val specialistId = Uuid.random()
    private val group = stubGroup(businessId, "Hair", createdAtMs = 0)
    private val haircut = stubService(group, "Haircut", 45.minutes, priceCents = 60_000)
    private val beardTrim = stubService(group, "Beard trim", 10.minutes, priceCents = 15_000)
    private val haircutId = haircut.id
    private val beardTrimId = beardTrim.id
    private val slotStart = Instant.parse("2026-11-24T12:30:00Z")
    private val raisedHaircut = haircut.copy(price = Money(80_000, Money.SupportedCurrency.UAH))

    private inner class Fixture {
        val dataSource = FakeBookingPreparationDataSource(approvedPricesOf(haircut, beardTrim))
        val cachedServices = MutableStateFlow(listOf(haircut, beardTrim))
        val getBusinessServices = mock<GetBusinessServices> {
            every { flow(businessId) } returns cachedServices
            everySuspend { refresh(businessId) } returns listOf(haircut, beardTrim)
        }
        val createQuote = mock<CreateQuote>()
        val createAppointmentRequest = mock<CreateAppointmentRequest>()
        val sut = SendBookingRequestImpl(
            dataSource = dataSource,
            getBookingPreparation = GetBookingPreparationImpl(dataSource, getBusinessServices),
            createQuote = createQuote,
            createAppointmentRequest = createAppointmentRequest,
            getBusinessServices = getBusinessServices,
        )

        fun selectAll() {
            dataSource.select(
                businessId,
                haircutId to 1,
                beardTrimId to 2,
                specialistId = specialistId,
                slotStart = slotStart,
            )
        }

        fun quote(services: List<Service> = listOf(haircut, beardTrim, beardTrim)) {
            everySuspend { createQuote(any(), any()) } returns
                Quote(id = Uuid.random(), services = services, token = QUOTE_TOKEN)
        }

        fun respond() {
            quote()
            everySuspend { createAppointmentRequest(any()) } returns Unit
        }

        fun failRequest(error: Throwable) {
            quote()
            everySuspend { createAppointmentRequest(any()) } throws error
        }
    }

    @Test
    fun `quotes every picked service once per unit`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.respond()

        whenn()
        fixture.sut(businessId, "")

        then()
        verifySuspend {
            fixture.createQuote(businessId, listOf(haircutId, beardTrimId, beardTrimId))
        }
    }

    @Test
    fun `sends the picked specialist, services and time with the quote and the note`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.selectAll()
            fixture.respond()

            whenn()
            fixture.sut(businessId, "  Window seat, please  ")

            then()
            verifySuspend {
                fixture.createAppointmentRequest(
                    AppointmentRequestDraft(
                        businessId = businessId,
                        employeeId = specialistId,
                        services = listOf(
                            RequestedService(haircutId, 1),
                            RequestedService(beardTrimId, 2),
                        ),
                        date = slotStart,
                        note = "Window seat, please",
                        offerToken = QUOTE_TOKEN,
                    ),
                )
            }
        }

    @Test
    fun `forgets the business's booking preparation once the request is sent`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.respond()

        whenn()
        fixture.sut(businessId, "")

        then()
        assertEquals(BookingSelection(), fixture.dataSource.selectionOf(businessId))
    }

    @Test
    fun `keeps the booking preparation when the request fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        val selection = fixture.dataSource.selectionOf(businessId)
        fixture.failRequest(CreateAppointmentRequest.Error.EmployeeBusy())

        whenn()
        runCatching { fixture.sut(businessId, "") }

        then()
        assertEquals(selection, fixture.dataSource.selectionOf(businessId))
    }

    @Test
    fun `throws selection incomplete without quoting when no time is picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 1, specialistId = specialistId)

        whenn()
        val call = suspend { fixture.sut(businessId, "") }

        then()
        assertFailsWith<Error.SelectionIncomplete> { call() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.createQuote(any(), any()) }
    }

    @Test
    fun `throws selection incomplete when no specialist is picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, haircutId to 1, slotStart = slotStart)

        whenn()
        val call = suspend { fixture.sut(businessId, "") }

        then()
        assertFailsWith<Error.SelectionIncomplete> { call() }
    }

    @Test
    fun `throws selection incomplete when no service is picked`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.dataSource.select(businessId, specialistId = specialistId, slotStart = slotStart)

        whenn()
        val call = suspend { fixture.sut(businessId, "") }

        then()
        assertFailsWith<Error.SelectionIncomplete> { call() }
    }

    @Test
    fun `throws services changed without quoting when a picked service is no longer offered`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.selectAll()
            fixture.cachedServices.value = listOf(haircut, beardTrim.copy(isAvailable = false))

            whenn()
            val call = suspend { fixture.sut(businessId, "") }

            then()
            assertFailsWith<Error.ServicesChanged> { call() }
            verifySuspend(VerifyMode.exactly(0)) { fixture.createQuote(any(), any()) }
        }

    @Test
    fun `throws prices changed without sending when only a quoted price differs`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.quote(listOf(raisedHaircut, beardTrim, beardTrim))

        whenn()
        val call = suspend { fixture.sut(businessId, "") }

        then()
        assertFailsWith<Error.PricesChanged> { call() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.createAppointmentRequest(any()) }
    }

    @Test
    fun `refreshes the business's services when a quoted price differs`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.quote(listOf(raisedHaircut, beardTrim, beardTrim))

        whenn()
        runCatching { fixture.sut(businessId, "") }

        then()
        verifySuspend { fixture.getBusinessServices.refresh(businessId) }
    }

    @Test
    fun `throws prices changed even when refreshing the services fails`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.quote(listOf(raisedHaircut, beardTrim, beardTrim))
        everySuspend { fixture.getBusinessServices.refresh(businessId) } throws
            IllegalStateException()

        whenn()
        val call = suspend { fixture.sut(businessId, "") }

        then()
        assertFailsWith<Error.PricesChanged> { call() }
    }

    @Test
    fun `sends the request at the new prices the client was shown`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.cachedServices.value = listOf(raisedHaircut, beardTrim)
        fixture.quote(listOf(raisedHaircut, beardTrim, beardTrim))
        everySuspend { fixture.createAppointmentRequest(any()) } returns Unit

        whenn()
        fixture.sut(businessId, "")

        then()
        verifySuspend { fixture.createAppointmentRequest(any()) }
    }

    @Test
    fun `refreshes the business's services when the quoted services changed`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.quote(listOf(haircut.copy(isAvailable = false), beardTrim, beardTrim))

        whenn()
        runCatching { fixture.sut(businessId, "") }

        then()
        verifySuspend { fixture.getBusinessServices.refresh(businessId) }
    }

    @Test
    fun `leaves the services alone when the time was taken`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.failRequest(CreateAppointmentRequest.Error.EmployeeBusy())

        whenn()
        runCatching { fixture.sut(businessId, "") }

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.getBusinessServices.refresh(any()) }
    }

    @Test
    fun `throws services changed without sending when a quoted duration differs`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.quote(listOf(haircut.copy(duration = 60.minutes), beardTrim, beardTrim))

        whenn()
        val call = suspend { fixture.sut(businessId, "") }

        then()
        assertFailsWith<Error.ServicesChanged> { call() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.createAppointmentRequest(any()) }
    }

    @Test
    fun `throws services changed without sending when the quote counts other services`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.selectAll()
            fixture.quote(listOf(haircut, beardTrim))

            whenn()
            val call = suspend { fixture.sut(businessId, "") }

            then()
            assertFailsWith<Error.ServicesChanged> { call() }
            verifySuspend(VerifyMode.exactly(0)) { fixture.createAppointmentRequest(any()) }
        }

    @Test
    fun `throws services changed without sending when a quoted service became unavailable`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            fixture.selectAll()
            fixture.quote(listOf(haircut.copy(isAvailable = false), beardTrim, beardTrim))

            whenn()
            val call = suspend { fixture.sut(businessId, "") }

            then()
            assertFailsWith<Error.ServicesChanged> { call() }
            verifySuspend(VerifyMode.exactly(0)) { fixture.createAppointmentRequest(any()) }
        }

    @Test
    fun `propagates cancellation without forgetting the booking preparation`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        val selection = fixture.dataSource.selectionOf(businessId)
        fixture.failRequest(CancellationException("cancelled"))

        whenn()
        val result = runCatching { fixture.sut(businessId, "") }

        then()
        assertFailsWith<CancellationException> { result.getOrThrow() }
        assertEquals(selection, fixture.dataSource.selectionOf(businessId))
    }

    @Test
    fun `throws time unavailable when the specialist got busy`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.EmployeeBusy(),
            Error.TimeUnavailable::class,
        )
    }

    @Test
    fun `throws time unavailable when the time no longer fits the business hours`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.TimeIsNotAllowed(),
            Error.TimeUnavailable::class,
        )
    }

    @Test
    fun `throws time unavailable when the business no longer works that day`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.DateIsNotAllowed(),
            Error.TimeUnavailable::class,
        )
    }

    @Test
    fun `throws time unavailable when the specialist no longer works that day`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.EmployeeDateIsNotAllowed(),
            Error.TimeUnavailable::class,
        )
    }

    @Test
    fun `throws time unavailable when the time no longer fits the specialist hours`() =
        runUnitTest {
            assertRequestErrorMapped(
                CreateAppointmentRequest.Error.EmployeeTimeIsNotAllowed(),
                Error.TimeUnavailable::class,
            )
        }

    @Test
    fun `throws time unavailable when the time has passed`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.DateInPast(),
            Error.TimeUnavailable::class,
        )
    }

    @Test
    fun `throws pending requests limit reached when the client waits on too many requests`() =
        runUnitTest {
            assertRequestErrorMapped(
                CreateAppointmentRequest.Error.PendingRequestsLimitReached(),
                Error.PendingRequestsLimitReached::class,
            )
        }

    @Test
    fun `throws prices changed when the price changed after quoting`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.PriceChanged(),
            Error.PricesChanged::class,
        )
    }

    @Test
    fun `throws services changed when the duration changed`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.DurationChanged(),
            Error.ServicesChanged::class,
        )
    }

    @Test
    fun `throws services changed when the services no longer match the quote`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.ServicesDoNotMatchOffer(),
            Error.ServicesChanged::class,
        )
    }

    @Test
    fun `throws services changed when the quote was already used`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.OfferAlreadyUsed(),
            Error.ServicesChanged::class,
        )
    }

    @Test
    fun `throws services changed when a service is gone from the request`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.ServiceNotFound(),
            Error.ServicesChanged::class,
        )
    }

    @Test
    fun `throws services changed when a service is gone from the quote`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        everySuspend { fixture.createQuote(any(), any()) } throws
            CreateQuote.Error.ServiceNotFound(IllegalStateException())

        whenn()
        val call = suspend { fixture.sut(businessId, "") }

        then()
        assertFailsWith<Error.ServicesChanged> { call() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.createAppointmentRequest(any()) }
    }

    @Test
    fun `throws specialist unavailable when the specialist left the business`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.EmployeeNotFound(),
            Error.SpecialistUnavailable::class,
        )
    }

    @Test
    fun `throws specialist unavailable when the specialist is suspended`() = runUnitTest {
        assertRequestErrorMapped(
            CreateAppointmentRequest.Error.EmployeeSuspended(),
            Error.SpecialistUnavailable::class,
        )
    }

    @Test
    fun `rethrows other errors`() = runUnitTest {
        assertRequestErrorMapped(IllegalStateException(), IllegalStateException::class)
    }

    private suspend fun assertRequestErrorMapped(
        error: Throwable,
        expected: KClass<out Throwable>,
    ) {
        given()
        val fixture = Fixture()
        fixture.selectAll()
        fixture.failRequest(error)

        whenn()
        val thrown = assertFailsWith<Throwable> { fixture.sut(businessId, "") }

        then()
        assertEquals(expected, thrown::class)
    }

    private companion object {
        const val QUOTE_TOKEN = "signed-quote"
    }
}
