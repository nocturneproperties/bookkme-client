package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.GetBookingInfo
import me.bookk.feature.appointments.domain.api.entity.BookingInfo
import me.bookk.feature.booking.domain.api.GetBusinessMessage.Error
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

class GetBusinessMessageImplTest {

    private val businessId = Uuid.random()

    private inner class Fixture {
        val getBookingInfo = mock<GetBookingInfo>()
        val sut = GetBusinessMessageImpl(getBookingInfo)

        fun cache(info: BookingInfo?) {
            every { getBookingInfo.flow(businessId) } returns flowOf(info)
        }
    }

    @Test
    fun `flow emits the note the business wrote for clients`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache(BookingInfo(businessId, "Please come 5 minutes early."))

        whenn()
        val message = fixture.sut.flow(businessId).first()

        then()
        assertEquals("Please come 5 minutes early.", message)
    }

    @Test
    fun `flow emits no message when the note is blank`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache(BookingInfo(businessId, "  \n "))

        whenn()
        val message = fixture.sut.flow(businessId).first()

        then()
        assertEquals(null, message)
    }

    @Test
    fun `flow emits no message while nothing is cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.cache(null)

        whenn()
        val message = fixture.sut.flow(businessId).first()

        then()
        assertEquals(null, message)
    }

    @Test
    fun `refresh refreshes the business's booking info`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookingInfo.refresh(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend { fixture.getBookingInfo.refresh(businessId) }
    }

    @Test
    fun `refresh throws booking unavailable when the business takes no appointments`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            everySuspend { fixture.getBookingInfo.refresh(businessId) } throws
                GetBookingInfo.Error.AppointmentsNotEnabled()

            whenn()
            val call = suspend { fixture.sut.refresh(businessId) }

            then()
            assertFailsWith<Error.BookingUnavailable> { call() }
        }

    @Test
    fun `refresh rethrows other errors`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBookingInfo.refresh(businessId) } throws IllegalStateException()

        whenn()
        val call = suspend { fixture.sut.refresh(businessId) }

        then()
        assertFailsWith<IllegalStateException> { call() }
    }
}
