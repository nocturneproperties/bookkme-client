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
import me.bookk.feature.appointments.domain.api.entity.AppointmentRequest
import me.bookk.feature.appointments.domain.api.entity.ClientAppointmentRequest
import me.bookk.feature.appointments.domain.datasource.ClientBookingDataSource
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

class GetClientAppointmentRequestImplTest {

    private val businessId = Uuid.random()
    private val request = stubAppointmentRequest(businessId = businessId)

    private class Fixture {
        val dataSource = mock<ClientBookingDataSource>()
        val getBusinesses = mock<GetBusinesses>()
        val sut = GetClientAppointmentRequestImpl(dataSource, getBusinesses)

        fun stubCache(id: Uuid, request: AppointmentRequest?, businesses: List<Business>) {
            every { dataSource.observeRequest(id) } returns flowOf(request)
            request?.let {
                every { getBusinesses.flow(setOf(it.businessId)) } returns flowOf(businesses)
            }
        }
    }

    @Test
    fun `emits the cached request named after its business`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(
            request.id,
            request,
            listOf(stubBusiness(id = businessId).copy(name = "Nail Room")),
        )

        whenn()
        val result = fixture.sut.flow(request.id).first()

        then()
        assertEquals(ClientAppointmentRequest(request, "Nail Room"), result)
    }

    @Test
    fun `leaves the business name empty while the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(request.id, request, emptyList())

        whenn()
        val result = fixture.sut.flow(request.id).first()

        then()
        assertEquals("", result?.businessName)
    }

    @Test
    fun `emits nothing while the request is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubCache(request.id, null, emptyList())

        whenn()
        val result = fixture.sut.flow(request.id).first()

        then()
        assertNull(result)
    }
}
