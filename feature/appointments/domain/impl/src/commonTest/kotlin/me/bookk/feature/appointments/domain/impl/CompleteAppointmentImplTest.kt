package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import library.money.api.Money
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.CompleteAppointment
import me.bookk.feature.appointments.domain.api.entity.AppointmentCompletedBy
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustmentDraft
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.reflect.KClass
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class CompleteAppointmentImplTest {

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
        val sut = CompleteAppointmentImpl(dataSource)
        val completed = stubAppointment().copy(
            status = AppointmentStatus.COMPLETED,
            completedBy = AppointmentCompletedBy.USER
        )

        fun stubSuccess() {
            everySuspend { dataSource.completeAppointment(any(), any()) } returns completed
            everySuspend { dataSource.saveAppointmentInDB(any()) } returns Unit
        }

        fun stubBusinessError(code: Int) {
            everySuspend { dataSource.completeAppointment(any(), any()) } throws DomainError.BusinessError(code, "msg")
        }
    }

    @Test
    fun `returns completed appointment from the backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSuccess()

        whenn()
        val result = fixture.sut(Uuid.random())

        then()
        assertEquals(fixture.completed, result)
    }

    @Test
    fun `passes price adjustment draft to datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointmentId = Uuid.random()
        val draft = PriceAdjustmentDraft(
            additionalServiceIds = listOf(Uuid.random()),
            price = Money(150.0, Money.SupportedCurrency.USD),
            reason = "Extra polish"
        )
        fixture.stubSuccess()

        whenn()
        fixture.sut(appointmentId, draft)

        then()
        verifySuspend { fixture.dataSource.completeAppointment(appointmentId, draft) }
    }

    @Test
    fun `completes without price adjustment by default`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointmentId = Uuid.random()
        fixture.stubSuccess()

        whenn()
        fixture.sut(appointmentId)

        then()
        verifySuspend { fixture.dataSource.completeAppointment(appointmentId, null) }
    }

    @Test
    fun `saves completed appointment in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSuccess()

        whenn()
        fixture.sut(Uuid.random())

        then()
        verifySuspend { fixture.dataSource.saveAppointmentInDB(fixture.completed) }
    }

    @Test
    fun `does not save in DB when backend rejects completion`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubBusinessError(AppointmentErrorCodes.APPOINTMENT_NOT_STARTED)

        whenn()
        runCatching { fixture.sut(Uuid.random()) }

        then()
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveAppointmentInDB(any()) }
    }

    @Test
    fun `throws AppointmentAlreadyCancelled on APPOINTMENT_ALREADY_CANCELED`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.APPOINTMENT_ALREADY_CANCELED, CompleteAppointment.Error.AppointmentAlreadyCancelled::class)
    }

    @Test
    fun `throws AppointmentNotStarted on APPOINTMENT_NOT_STARTED`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.APPOINTMENT_NOT_STARTED, CompleteAppointment.Error.AppointmentNotStarted::class)
    }

    @Test
    fun `throws AppointmentMarkedNoShow on APPOINTMENT_MARKED_NO_SHOW`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.APPOINTMENT_MARKED_NO_SHOW, CompleteAppointment.Error.AppointmentMarkedNoShow::class)
    }

    @Test
    fun `throws NegativePrice on PRICE_ADJUSTMENT_NEGATIVE_PRICE`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.PRICE_ADJUSTMENT_NEGATIVE_PRICE, CompleteAppointment.Error.NegativePrice::class)
    }

    @Test
    fun `throws CurrencyMismatch on PRICE_ADJUSTMENT_CURRENCY_MISMATCH`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.PRICE_ADJUSTMENT_CURRENCY_MISMATCH, CompleteAppointment.Error.CurrencyMismatch::class)
    }

    @Test
    fun `throws ReasonTooLong on PRICE_ADJUSTMENT_REASON_TOO_LONG`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.PRICE_ADJUSTMENT_REASON_TOO_LONG, CompleteAppointment.Error.ReasonTooLong::class)
    }

    @Test
    fun `throws ServiceNotFound on BUSINESS_QUOTE_SERVICE_NOT_FOUND`() = runUnitTest {
        assertMapped(AppointmentErrorCodes.BUSINESS_QUOTE_SERVICE_NOT_FOUND, CompleteAppointment.Error.ServiceNotFound::class)
    }

    private suspend fun assertMapped(code: Int, expected: KClass<out Throwable>) {
        given()
        val fixture = Fixture()
        fixture.stubBusinessError(code)

        whenn()
        val error = assertFailsWith<Throwable> { fixture.sut(Uuid.random()) }

        then()
        assertEquals(expected, error::class)
    }
}
