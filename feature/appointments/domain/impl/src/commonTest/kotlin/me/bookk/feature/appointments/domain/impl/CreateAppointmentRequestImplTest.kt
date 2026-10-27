package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
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
import me.bookk.feature.appointments.domain.api.CreateAppointmentRequest.Error
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequestDraft
import me.bookk.feature.appointments.domain.api.entity.RequestedService
import me.bookk.feature.appointments.domain.datasource.AppointmentRequestDataSource
import kotlin.reflect.KClass
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class CreateAppointmentRequestImplTest {

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
        val sut = CreateAppointmentRequestImpl(dataSource)

        fun draft(services: List<RequestedService> = listOf(RequestedService(Uuid.random(), 1))) =
            AppointmentRequestDraft(
                businessId = Uuid.random(),
                employeeId = Uuid.random(),
                services = services,
                date = Instant.fromEpochMilliseconds(1_800_000_000_000),
                note = "Window seat",
                offerToken = "signed-offer"
            )

        fun stubSuccess() {
            everySuspend { dataSource.createAppointmentRequest(any()) } returns Unit
        }

        fun stubBusinessError(code: Int) {
            everySuspend { dataSource.createAppointmentRequest(any()) } throws DomainError.BusinessError(code, "msg")
        }
    }

    @Test
    fun `sends the draft to the datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val draft = fixture.draft()
        fixture.stubSuccess()

        whenn()
        fixture.sut(draft)

        then()
        verifySuspend { fixture.dataSource.createAppointmentRequest(draft) }
    }

    @Test
    fun `merges entries for the same service into one count in first-booked order`() = runUnitTest {
        given()
        val fixture = Fixture()
        val haircut = Uuid.random()
        val beard = Uuid.random()
        val draft = fixture.draft(
            services = listOf(RequestedService(haircut, 1), RequestedService(beard, 1), RequestedService(haircut, 2))
        )
        fixture.stubSuccess()

        whenn()
        fixture.sut(draft)

        then()
        verifySuspend {
            fixture.dataSource.createAppointmentRequest(
                draft.copy(services = listOf(RequestedService(haircut, 3), RequestedService(beard, 1)))
            )
        }
    }

    @Test
    fun `throws RequestForThisTimeExists on REQUEST_EXISTS`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.REQUEST_EXISTS, Error.RequestForThisTimeExists::class)
    }

    @Test
    fun `throws TimeIsNotAllowed on TIME_NOT_ALLOWED`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.TIME_NOT_ALLOWED, Error.TimeIsNotAllowed::class)
    }

    @Test
    fun `throws DateIsNotAllowed on DATE_NOT_ALLOWED`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.DATE_NOT_ALLOWED, Error.DateIsNotAllowed::class)
    }

    @Test
    fun `throws DateInPast on DATE_IN_PAST`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.DATE_IN_PAST, Error.DateInPast::class)
    }

    @Test
    fun `throws PriceChanged on PRICE_CHANGED`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.PRICE_CHANGED, Error.PriceChanged::class)
    }

    @Test
    fun `throws DurationChanged on DURATION_CHANGED`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.DURATION_CHANGED, Error.DurationChanged::class)
    }

    @Test
    fun `throws ServicesDoNotMatchOffer on SERVICES_VALIDATION_FAILED`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.SERVICES_VALIDATION_FAILED, Error.ServicesDoNotMatchOffer::class)
    }

    @Test
    fun `throws OfferAlreadyUsed on QUOTE_TOKEN_ALREADY_USED`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.QUOTE_TOKEN_ALREADY_USED, Error.OfferAlreadyUsed::class)
    }

    @Test
    fun `throws ServiceNotFound on BUSINESS_QUOTE_SERVICE_NOT_FOUND`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.BUSINESS_QUOTE_SERVICE_NOT_FOUND, Error.ServiceNotFound::class)
    }

    @Test
    fun `throws EmployeeNotFound on BUSINESS_EMPLOYEE_NOT_EXISTS`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.BUSINESS_EMPLOYEE_NOT_EXISTS, Error.EmployeeNotFound::class)
    }

    @Test
    fun `throws EmployeeSuspended on BUSINESS_EMPLOYEE_SUSPENDED`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.BUSINESS_EMPLOYEE_SUSPENDED, Error.EmployeeSuspended::class)
    }

    @Test
    fun `rethrows unmapped business errors`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubBusinessError(999_999)

        whenn()
        val error = assertFailsWith<DomainError.BusinessError> { fixture.sut(fixture.draft()) }

        then()
        assertEquals(999_999, error.errorCode)
    }

    private suspend fun assertMapped(code: Int, expected: KClass<out Throwable>) {
        given()
        val fixture = Fixture()
        fixture.stubBusinessError(code)

        whenn()
        val error = assertFailsWith<Throwable> { fixture.sut(fixture.draft()) }

        then()
        assertEquals(expected, error::class)
    }
}
