package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetBookingInfo
import me.bookk.feature.appointments.domain.api.entity.BookingInfo
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

class GetBookingInfoImplTest {

    private val businessId = Uuid.random()
    private val info = BookingInfo(businessId, "Please come 5 minutes early.")

    private inner class Fixture {
        val dataSource = mock<ClientBookingDataSource> {
            everySuspend { saveBookingInfoInDb(any()) } returns Unit
            everySuspend { deleteBookingInfoInDb(any()) } returns Unit
        }
        val sut = GetBookingInfoImpl(dataSource)
    }

    @Test
    fun `flow emits the cached booking info of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.observeBookingInfo(businessId) } returns flowOf(info)

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertEquals(info, result)
    }

    @Test
    fun `flow emits null while nothing is cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.dataSource.observeBookingInfo(businessId) } returns flowOf(null)

        whenn()
        val result = fixture.sut.flow(businessId).first()

        then()
        assertEquals(null, result)
    }

    @Test
    fun `refresh saves the fetched booking info`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getBookingInfo(businessId) } returns info

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend { fixture.dataSource.saveBookingInfoInDb(info) }
    }

    @Test
    fun `refresh forgets the cached info and throws when the business takes no appointments`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            everySuspend { fixture.dataSource.getBookingInfo(businessId) } throws
                DomainError.NotFound(IllegalStateException())

            whenn()
            val call = suspend { fixture.sut.refresh(businessId) }

            then()
            assertFailsWith<GetBookingInfo.Error.AppointmentsNotEnabled> { call() }
            verifySuspend { fixture.dataSource.deleteBookingInfoInDb(businessId) }
        }

    @Test
    fun `refresh keeps the cached info when the request fails otherwise`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.dataSource.getBookingInfo(businessId) } throws
            DomainError.NoConnectionError(IllegalStateException())

        whenn()
        val call = suspend { fixture.sut.refresh(businessId) }

        then()
        assertFailsWith<DomainError.NoConnectionError> { call() }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteBookingInfoInDb(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveBookingInfoInDb(any()) }
    }
}
