package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import dev.mokkery.verify.VerifyMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.BeforeTest
import kotlin.test.AfterTest
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import dev.mokkery.every
import dev.mokkery.matcher.matches
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.LocalDateTime
import me.bookk.feature.appointments.domain.api.CreateAppointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentDraft
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.services.domain.api.quote.CreateQuote
import me.bookk.feature.services.domain.api.quote.entity.Quote
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import kotlin.time.Instant
import kotlin.time.Duration.Companion.minutes
import library.money.api.Money

@OptIn(ExperimentalCoroutinesApi::class)
class CreateAppointmentImplTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    @BeforeTest
    fun setUp() { Dispatchers.setMain(testDispatcher) }
    @AfterTest
    fun tearDown() { Dispatchers.resetMain() }

    private class Fixture {
        val businessId = Uuid.parse("00000000-0000-0000-0000-000000000001")
        val employeeId = Uuid.parse("00000000-0000-0000-0000-000000000002")
        val appointmentDataSource = mock<AppointmentDataSource>()
        val createQuote = mock<CreateQuote>()
        val getServices = mock<GetServices>()
        val businesses = mock<ObserveUserBusinessesChanges>()
        val sut = CreateAppointmentImpl(appointmentDataSource, createQuote, getServices, businesses)
        val draft = AppointmentDraft(businessId, stubClientSnapshot(), listOf(stubServiceSnapshot()), LocalDateTime(2026, 11, 5, 10, 0), " note ")
        val snapshot = draft.services.single()
        val service = Service(snapshot.id, businessId, ServiceGroup(snapshot.groupId, businessId, "Group", Instant.fromEpochMilliseconds(0)), snapshot.name, snapshot.duration, snapshot.price, true, Instant.fromEpochMilliseconds(0))
        val quote = Quote(businessId, listOf(service), "fresh-offer")
        val created = stubAppointment(businessId = businessId)
        init {
            every { businesses.invoke() } returns flowOf(listOf(stubBusiness(id = businessId, employeeId = employeeId)))
            everySuspend { createQuote(any(), any()) } returns quote
            everySuspend { getServices.refresh(any()) } returns listOf(service)
            everySuspend { appointmentDataSource.createAppointment(any()) } returns created
            everySuspend { appointmentDataSource.saveAppointmentInDB(any()) } returns Unit
        }
    }
    @Test
    fun `books with a fresh matching quote and saves the server response`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = fixture.sut(fixture.draft)

        then()
        assertEquals(fixture.created, result)
        verifySuspend { fixture.createQuote(fixture.businessId, fixture.draft.services.map { it.id }.distinct()) }
        verifySuspend { fixture.appointmentDataSource.createAppointment(matches({ "ID-only booking" }) {
            it.businessId == fixture.businessId && it.employeeId == fixture.employeeId && it.clientId == fixture.draft.client.id &&
                it.offerToken == "fresh-offer" && it.note == "note"
        }) }
        verifySuspend { fixture.appointmentDataSource.saveAppointmentInDB(fixture.created) }
    }

    @Test
    fun `groups duplicate service snapshots into booking counts`() = runUnitTest {
        given()
        val fixture = Fixture()
        val draft = fixture.draft.copy(services = List(3) { fixture.snapshot })

        whenn()
        fixture.sut(draft)

        then()
        verifySuspend { fixture.appointmentDataSource.createAppointment(matches({ "counts" }) {
            it.services.single().serviceId == fixture.snapshot.id && it.services.single().count == 3
        }) }
    }

    @Test
    fun `uses an explicitly selected employee`() = runUnitTest {
        given()
        val fixture = Fixture()
        val selected = Uuid.parse("00000000-0000-0000-0000-000000000003")

        whenn()
        fixture.sut(fixture.draft.copy(employeeId = selected))

        then()
        verifySuspend { fixture.appointmentDataSource.createAppointment(matches({ "selected employee" }) { it.employeeId == selected }) }
    }

    @Test
    fun `rejects an empty service list without quoting or booking`() = runUnitTest {
        given()
        val fixture = Fixture()

        whenn()
        val result = runCatching { fixture.sut(fixture.draft.copy(services = emptyList())) }

        then()
        assertFailsWith<CreateAppointment.Error.EmptyServices> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.createQuote(any(), any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.createAppointment(any()) }
    }

    @Test
    fun `rejects a missing employee assignment before booking`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.businesses.invoke() } returns flowOf(emptyList())

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.NotAnEmployee> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.createAppointment(any()) }
    }
    @Test
    fun `refreshes backend services and rejects quote price mismatch`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createQuote(any(), any()) } returns fixture.quote.copy(services = listOf(fixture.service.copy(price = Money(999L, Money.SupportedCurrency.USD))))

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.PriceChanged> { result.getOrThrow() }
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.createAppointment(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `refreshes backend services and rejects quote duration mismatch`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createQuote(any(), any()) } returns fixture.quote.copy(services = listOf(fixture.service.copy(duration = 45.minutes)))

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.DurationChanged> { result.getOrThrow() }
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.createAppointment(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `refreshes backend services and rejects quote service selection mismatch`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createQuote(any(), any()) } returns fixture.quote.copy(services = emptyList())

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.ServicesChanged> { result.getOrThrow() }
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.createAppointment(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `refreshes backend services and rejects quote metadata mismatch`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createQuote(any(), any()) } returns fixture.quote.copy(services = listOf(fixture.service.copy(name = "Changed")))

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.ServicesChanged> { result.getOrThrow() }
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.createAppointment(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 300004 to AppointmentOverlap`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(300004, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.AppointmentOverlap> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 300002 to TimeIsNotAllowed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(300002, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.TimeIsNotAllowed> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 300003 to DateIsNotAllowed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(300003, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.DateIsNotAllowed> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 300012 to DateInPast`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(300012, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.DateInPast> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 300013 to PriceChanged`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(300013, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.PriceChanged> { result.getOrThrow() }
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 300017 to DurationChanged`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(300017, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.DurationChanged> { result.getOrThrow() }
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 300014 to ServicesChanged`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(300014, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.ServicesChanged> { result.getOrThrow() }
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 300016 to OfferAlreadyUsed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(300016, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.OfferAlreadyUsed> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 200013 to ServiceNotFound`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(200013, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.ServiceNotFound> { result.getOrThrow() }
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 200014 to EmptyServices`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(200014, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.EmptyServices> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 200024 to EmployeeNotFound`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(200024, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.EmployeeNotFound> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 200006 to ClientNotFound`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(200006, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.ClientNotFound> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `maps booking error 200033 to EmployeeSuspended`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.appointmentDataSource.createAppointment(any()) } throws me.bookk.core.domain.entity.Error.BusinessError(200033, "rejected")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.EmployeeSuspended> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.saveAppointmentInDB(any()) }
    }
    @Test
    fun `a service removed before quoting refreshes the cache and does not book`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createQuote(any(), any()) } throws CreateQuote.Error.ServiceNotFound(IllegalStateException("removed"))

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<CreateAppointment.Error.ServiceNotFound> { result.getOrThrow() }
        verifySuspend { fixture.getServices.refresh(fixture.businessId) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.createAppointment(any()) }
    }

    @Test
    fun `a failed cache refresh is reported without booking`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.createQuote(any(), any()) } returns fixture.quote.copy(services = emptyList())
        everySuspend { fixture.getServices.refresh(any()) } throws IllegalStateException("refresh failed")

        whenn()
        val result = runCatching { fixture.sut(fixture.draft) }

        then()
        assertFailsWith<IllegalStateException> { result.getOrThrow() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.appointmentDataSource.createAppointment(any()) }
    }

}
