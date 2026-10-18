package me.bookk.feature.appointments.data.remote.api

import io.ktor.resources.href
import io.ktor.resources.serialization.ResourcesFormat
import kotlinx.datetime.LocalDate
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.data.remote.api.AppointmentRouting.Api
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
        val resource = Api.Appointment.List(businessId = businessId, date = date, employeeId = employeeId)

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals("/api/appointments/list/$businessId?date=2026-10-03&employeeId=$employeeId", url)
    }

    @Test
    fun `own requests point to the mine endpoint of the business`() = runUnitTest {
        given()
        val resource = Api.Appointment.Requests.Own(Api.Appointment.Requests(businessId = businessId))

        whenn()
        val url = href(ResourcesFormat(), resource)

        then()
        assertEquals("/api/appointments/request/$businessId/mine", url)
    }
}
