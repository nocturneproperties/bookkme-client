package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.appointments.domain.api.entity.Appointment
import me.bookk.feature.appointments.domain.api.entity.ClientAppointment
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class GetClientAppointmentImplTest {

    private val businessId = Uuid.random()
    private val appointment = stubAppointment(businessId = businessId)

    private class Fixture {
        val dataSource = mock<ClientBookingDataSource>()
        val getBusinesses = mock<GetBusinesses>()
        val sut = GetClientAppointmentImpl(dataSource, getBusinesses)

        fun stubCache(id: Uuid, appointment: Appointment?, businesses: List<Business>) {
            every { dataSource.observeAppointment(id) } returns flowOf(appointment)
            appointment?.let {
                every { getBusinesses.flow(setOf(it.businessId)) } returns flowOf(businesses)
            }
        }
    }

    @Test
    fun `emits the cached appointment named after its business`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(
            appointment.id,
            appointment,
            listOf(stubBusiness(id = businessId).copy(name = "Nail Room")),
        )

        whenn()
        val result = fixture.sut.flow(appointment.id).first()

        then()
        assertEquals(ClientAppointment(appointment, "Nail Room"), result)
    }

    @Test
    fun `leaves the business name empty while the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(appointment.id, appointment, emptyList())

        whenn()
        val result = fixture.sut.flow(appointment.id).first()

        then()
        assertEquals("", result?.businessName)
    }

    @Test
    fun `emits nothing while the appointment is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(appointment.id, null, emptyList())

        whenn()
        val result = fixture.sut.flow(appointment.id).first()

        then()
        assertNull(result)
    }
}
