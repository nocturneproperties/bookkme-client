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
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.MarkAppointmentNoShow
import me.bookk.feature.appointments.domain.api.entity.AppointmentErrorCodes
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus
import me.bookk.feature.appointments.domain.datasource.AppointmentDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class MarkAppointmentNoShowImplTest {

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
        val sut = MarkAppointmentNoShowImpl(dataSource)
        val noShow = stubAppointment().copy(status = AppointmentStatus.NO_SHOW)

        fun stubSuccess() {
            everySuspend { dataSource.markAppointmentNoShow(any()) } returns noShow
            everySuspend { dataSource.saveAppointmentInDB(any()) } returns Unit
        }
    }

    @Test
    fun `returns no-show appointment from the backend`() = runUnitTest {
        given()
        val fixture = Fixture()
        val appointmentId = Uuid.random()
        fixture.stubSuccess()

        whenn()
        val result = fixture.sut(appointmentId)

        then()
        assertEquals(fixture.noShow, result)
        verifySuspend { fixture.dataSource.markAppointmentNoShow(appointmentId) }
    }

    @Test
    fun `saves no-show appointment in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubSuccess()

        whenn()
        fixture.sut(Uuid.random())

        then()
        verifySuspend { fixture.dataSource.saveAppointmentInDB(fixture.noShow) }
    }

    @Test
    fun `throws AppointmentAlreadyCancelled on APPOINTMENT_ALREADY_CANCELED`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.markAppointmentNoShow(any()) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_ALREADY_CANCELED, "msg")

        whenn()
        then()
        assertFailsWith<MarkAppointmentNoShow.Error.AppointmentAlreadyCancelled> { fixture.sut(Uuid.random()) }
        verifySuspend(VerifyMode.not) { fixture.dataSource.saveAppointmentInDB(any()) }
    }

    @Test
    fun `throws AppointmentNotStarted on APPOINTMENT_NOT_STARTED`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.markAppointmentNoShow(any()) } throws
            DomainError.BusinessError(AppointmentErrorCodes.APPOINTMENT_NOT_STARTED, "msg")

        whenn()
        then()
        assertFailsWith<MarkAppointmentNoShow.Error.AppointmentNotStarted> { fixture.sut(Uuid.random()) }
    }
}
