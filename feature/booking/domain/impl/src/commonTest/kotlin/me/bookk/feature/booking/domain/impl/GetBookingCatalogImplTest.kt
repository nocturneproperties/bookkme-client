package me.bookk.feature.booking.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.booking.domain.api.entity.BookingCatalog
import me.bookk.feature.booking.domain.api.entity.BookingServiceGroup
import me.bookk.feature.business.domain.api.business.GetBusinesses
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.minutes
import kotlin.uuid.Uuid

class GetBookingCatalogImplTest {

    private val businessId = Uuid.random()
    private val hair = stubGroup(businessId, "Hair", createdAtMs = 1_000)
    private val beard = stubGroup(businessId, "Beard", createdAtMs = 2_000)
    private val haircut = stubService(hair, "Haircut", 45.minutes, 60_000)
    private val kidsHaircut = stubService(hair, "Kids haircut", 30.minutes, 40_000)
    private val beardTrim = stubService(beard, "Beard trim", 20.minutes, 30_000)

    private inner class Fixture {
        val businesses = MutableStateFlow<List<Business>>(emptyList())
        val services = MutableStateFlow<List<Service>>(emptyList())
        val getBusinesses = mock<GetBusinesses> {
            every { flow(setOf(businessId)) } returns businesses
        }
        val getBusinessServices = mock<GetBusinessServices> {
            every { flow(businessId) } returns services
        }
        val sut = GetBookingCatalogImpl(getBusinesses, getBusinessServices)
    }

    private fun Service.inGroup(): BookingServiceGroup {
        return BookingServiceGroup(group.id, group.name, listOf(toExpectedBookingService()))
    }

    @Test
    fun `names the business and lists its services grouped in group order`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.businesses.value = listOf(stubBusiness(businessId, "Studio Lumen"))
        fixture.services.value = listOf(beardTrim, haircut, kidsHaircut)

        whenn()
        val catalog = fixture.sut.flow(businessId).first()

        then()
        assertEquals(
            BookingCatalog(
                businessName = "Studio Lumen",
                groups = listOf(
                    BookingServiceGroup(
                        hair.id,
                        "Hair",
                        listOf(
                            haircut.toExpectedBookingService(),
                            kidsHaircut.toExpectedBookingService(),
                        ),
                    ),
                    beardTrim.inGroup(),
                ),
            ),
            catalog,
        )
    }

    @Test
    fun `leaves out services the business does not offer`() = runUnitTest {
        given()
        val fixture = Fixture()
        val hidden = stubService(hair, "Hidden", 30.minutes, 10_000, isAvailable = false)
        val hiddenBeard =
            stubService(beard, "Hidden beard", 30.minutes, 10_000, isAvailable = false)
        fixture.services.value = listOf(haircut, hidden, hiddenBeard)

        whenn()
        val catalog = fixture.sut.flow(businessId).first()

        then()
        assertEquals(listOf(haircut.inGroup()), catalog.groups)
    }

    @Test
    fun `has no business name while the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.services.value = listOf(haircut)

        whenn()
        val catalog = fixture.sut.flow(businessId).first()

        then()
        assertEquals(null, catalog.businessName)
    }

    @Test
    fun `refreshes the business and its services and returns the catalog`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend {
            fixture.getBusinesses.refresh(setOf(businessId))
        } returns listOf(stubBusiness(businessId, "Studio Lumen"))
        everySuspend { fixture.getBusinessServices.refresh(businessId) } returns listOf(haircut)

        whenn()
        val catalog = fixture.sut.refresh(businessId)

        then()
        assertEquals(BookingCatalog("Studio Lumen", listOf(haircut.inGroup())), catalog)
    }

    @Test
    fun `fails the refresh when the business cannot be refreshed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBusinesses.refresh(setOf(businessId)) } throws TestFailure()
        everySuspend { fixture.getBusinessServices.refresh(businessId) } returns listOf(haircut)

        whenn()
        val result = runCatching { fixture.sut.refresh(businessId) }

        then()
        assertFailsWith<TestFailure> { result.getOrThrow() }
    }

    @Test
    fun `fails the refresh when the services cannot be refreshed`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.getBusinesses.refresh(setOf(businessId)) } returns emptyList()
        everySuspend { fixture.getBusinessServices.refresh(businessId) } throws TestFailure()

        whenn()
        val result = runCatching { fixture.sut.refresh(businessId) }

        then()
        assertFailsWith<TestFailure> { result.getOrThrow() }
    }

    private class TestFailure : Exception()
}
