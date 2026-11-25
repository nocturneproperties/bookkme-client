package me.bookk.feature.appointments.data.remote.api

import io.ktor.resources.href
import io.ktor.resources.serialization.ResourcesFormat
import kotlinx.datetime.LocalDate
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting.Api
import me.bookk.feature.appointments.data.remote.model.AppointmentRequestStatusRemote
import me.bookk.feature.appointments.data.remote.model.AppointmentStatusRemote
import me.bookk.feature.appointments.data.remote.model.BookingPeriodRemote
import me.bookk.feature.appointments.data.remote.model.HistoryOrderRemote
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class AppointmentRoutingTest {

    private val businessId = Uuid.parse("6f1c2d3e-0000-4000-8000-000000000001")
    private val employeeId = Uuid.parse("6f1c2d3e-0000-4000-8000-000000000002")
    private val date = LocalDate(2026, 10, 3)

    @Test
    fun `list of a day sends only the date when no employee is selected`() = runUnitTest {
        given()
        val resource = Api.Appointment.List(businessId = businessId, date = date)

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals("/api/appointments/list/$businessId?date=2026-10-03", url)
    }

    @Test
    fun `list of a day sends the employee filter when an employee is selected`() = runUnitTest {
        given()
        val resource = Api.Appointment.List(
            businessId = businessId,
            date = date,
            employeeId = employeeId,
        )

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals(
            "/api/appointments/list/$businessId?date=2026-10-03&employeeId=$employeeId",
            url,
        )
    }

    @Test
    fun `assigned requests ask the request history for the employee's pending ones, soonest first`() =
        runUnitTest {
            given()
            val resource = Api.Appointment.RequestHistory(
                businessId = businessId,
                employeeId = employeeId,
                status = listOf(AppointmentRequestStatusRemote.PENDING),
                order = HistoryOrderRemote.SOONEST_FIRST,
            )

            whenn()
            val url = href(ResourcesFormat(), resource)

            then()
            assertEquals(
                "/api/appointments/request/history/$businessId" +
                    "?employeeId=$employeeId&status=PENDING&order=SOONEST_FIRST",
                url,
            )
        }

    @Test
    fun `request history sends the cursor of the next page`() = runUnitTest {
        given()
        val resource = Api.Appointment.RequestHistory(businessId = businessId, cursor = "after-a")

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals("/api/appointments/request/history/$businessId?cursor=after-a", url)
    }

    @Test
    fun `history asks for the first page without paging parameters`() = runUnitTest {
        given()
        val resource = Api.Appointment.History(businessId = businessId)

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals("/api/appointments/history/$businessId", url)
    }

    @Test
    fun `history sends the cursor of the next page and the query`() = runUnitTest {
        given()
        val resource = Api.Appointment.History(
            businessId = businessId,
            cursor = "after-a",
            query = "anna",
        )

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals("/api/appointments/history/$businessId?cursor=after-a&query=anna", url)
    }

    @Test
    fun `withdrawing a request points to the withdraw endpoint of the request`() = runUnitTest {
        given()
        val requestId = Uuid.parse("6f1c2d3e-0000-4000-8000-000000000003")
        val resource = Api.Appointment.RequestWithdraw(id = requestId)

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals("/api/appointments/request/$requestId/withdraw", url)
    }

    @Test
    fun `own appointments send the period and every status`() = runUnitTest {
        given()
        val resource = Api.Appointment.Mine(
            cursor = "after-a",
            period = BookingPeriodRemote.UPCOMING,
            status = listOf(AppointmentStatusRemote.COMPLETED, AppointmentStatusRemote.NO_SHOW),
        )

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals(
            "/api/appointments/mine?cursor=after-a&period=UPCOMING&status=COMPLETED&status=NO_SHOW",
            url,
        )
    }

    @Test
    fun `own requests send the period and every status`() = runUnitTest {
        given()
        val resource = Api.Appointment.RequestMine(
            period = BookingPeriodRemote.PAST,
            status = listOf(AppointmentRequestStatusRemote.DECLINED),
        )

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals("/api/appointments/request/mine?period=PAST&status=DECLINED", url)
    }
}
