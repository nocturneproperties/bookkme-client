package me.bookk.feature.services.domain.impl.service

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.impl.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetServicesImplTest {

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
        val getBusinessServices = mock<GetBusinessServices>()
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges>()
        val sut = GetServicesImpl(getBusinessServices, observeDashboardBusinessChanges)
    }

    private fun stubBusiness(id: Uuid = Uuid.random()) = Business(
        id = id,
        name = "Business",
        description = "",
        address = "",
        location = null,
        currency = Currency("USD"),
        timeZone = TimeZone.UTC,
        socials = emptyMap(),
        schedule = WorkingSchedule(),
        permissions = BusinessPermissions(
            business = ResourcePermission(),
            employees = ResourcePermission(),
            clients = ResourcePermission(),
            services = ResourcePermission(),
            appointments = ResourcePermission(),
        ),
    )

    @Test
    fun `flow emits empty list when there is no dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(null)

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(emptyList(), result)
    }

    @Test
    fun `flow emits the services of the current dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val services = listOf(stubService(business.id), stubService(business.id))
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.getBusinessServices.flow(business.id) } returns flowOf(services)

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(services, result)
    }

    @Test
    fun `flow re-resolves the db observation when the dashboard business changes`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businesses = MutableSharedFlow<Business?>(replay = 1)
        val firstBusiness = stubBusiness()
        val secondBusiness = stubBusiness()
        val firstServices = listOf(stubService(firstBusiness.id))
        val secondServices = listOf(stubService(secondBusiness.id))
        businesses.tryEmit(firstBusiness)
        every { fixture.observeDashboardBusinessChanges() } returns businesses
        every { fixture.getBusinessServices.flow(firstBusiness.id) } returns
            flowOf(firstServices)
        every { fixture.getBusinessServices.flow(secondBusiness.id) } returns
            flowOf(secondServices)
        val results = mutableListOf<List<Service>>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut.flow().collect { results.add(it) }
        }

        whenn()
        businesses.emit(secondBusiness)

        then()
        job.cancel()
        assertEquals(firstServices, results.first())
        assertEquals(secondServices, results.last())
    }

    @Test
    fun `refresh refreshes the services of the given business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val remote = listOf(stubService(businessId))
        everySuspend { fixture.getBusinessServices.refresh(businessId) } returns remote

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(remote, result)
    }

    @Test
    fun `refresh propagates a fetch error to the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val error = IllegalStateException("network down")
        everySuspend { fixture.getBusinessServices.refresh(businessId) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> { fixture.sut.refresh(businessId) }

        then()
        assertEquals(error, thrown)
    }

    @Test
    fun `refreshes the business services when the business data fetch runs`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.getBusinessServices.refresh(businessId) } returns emptyList()

        whenn()
        fixture.sut.doOnBusinessDataFetch(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.getBusinessServices.refresh(businessId) }
    }
}
