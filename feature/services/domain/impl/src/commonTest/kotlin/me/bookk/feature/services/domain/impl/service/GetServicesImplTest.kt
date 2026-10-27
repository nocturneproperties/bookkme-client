package me.bookk.feature.services.domain.impl.service

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
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
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import me.bookk.feature.services.domain.impl.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
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
        val serviceDataSource = mock<ServiceDataSource>()
        val groupDataSource = mock<ServiceGroupDataSource>()
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges>()
        val sut = GetServicesImpl(serviceDataSource, groupDataSource, observeDashboardBusinessChanges)
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
            appointments = ResourcePermission()
        )
    )

    private fun stubTimedService(businessId: Uuid, createdAtMs: Long) =
        stubService(businessId).let { s ->
            Service(
                id = s.id, businessId = s.businessId, group = s.group, name = s.name,
                duration = s.duration, price = s.price, isAvailable = s.isAvailable,
                createdAt = Instant.fromEpochMilliseconds(createdAtMs)
            )
        }

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
    fun `flow emits the db services sorted by createdAt for the current dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val older = stubTimedService(business.id, 1000L)
        val newer = stubTimedService(business.id, 2000L)
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.serviceDataSource.observeServicesDBChanges(business.id) } returns flowOf(listOf(newer, older))

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(listOf(older, newer), result)
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
        every { fixture.serviceDataSource.observeServicesDBChanges(firstBusiness.id) } returns flowOf(firstServices)
        every { fixture.serviceDataSource.observeServicesDBChanges(secondBusiness.id) } returns flowOf(secondServices)
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
    fun `flow never triggers a network fetch as a side effect of being collected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.serviceDataSource.observeServicesDBChanges(business.id) } returns flowOf(emptyList())

        whenn()
        fixture.sut.flow().first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.serviceDataSource.getServices(any()) }
    }

    @Test
    fun `refresh fetches services from remote, saves them and their groups, and returns them sorted`() =
        runUnitTest {
            given()
            val fixture = Fixture()
            val businessId = Uuid.random()
            val older = stubTimedService(businessId, 1000L)
            val newer = stubTimedService(businessId, 2000L)
            everySuspend { fixture.serviceDataSource.getServices(businessId) } returns listOf(newer, older)
            everySuspend { fixture.serviceDataSource.getServiceIdsInDb(businessId) } returns listOf(older.id, newer.id)
            everySuspend { fixture.groupDataSource.saveGroupsInDB(any()) } returns Unit
            everySuspend { fixture.serviceDataSource.saveServicesInDB(any()) } returns Unit
            everySuspend { fixture.serviceDataSource.saveLastSyncedAt(businessId) } returns Unit

            whenn()
            val result = fixture.sut.refresh(businessId)

            then()
            assertEquals(listOf(older, newer), result)
            verifySuspend { fixture.serviceDataSource.saveServicesInDB(listOf(newer, older)) }
            verifySuspend { fixture.groupDataSource.saveGroupsInDB(any()) }
            verifySuspend { fixture.serviceDataSource.saveLastSyncedAt(businessId) }
        }

    @Test
    fun `refresh deletes local services that are no longer present remotely`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val stillPresent = stubService(businessId)
        val remote = listOf(stillPresent)
        val staleId = Uuid.random()
        everySuspend { fixture.serviceDataSource.getServices(businessId) } returns remote
        everySuspend { fixture.serviceDataSource.getServiceIdsInDb(businessId) } returns listOf(stillPresent.id, staleId)
        everySuspend { fixture.serviceDataSource.deleteServicesInDb(listOf(staleId)) } returns Unit
        everySuspend { fixture.groupDataSource.saveGroupsInDB(any()) } returns Unit
        everySuspend { fixture.serviceDataSource.saveServicesInDB(remote) } returns Unit
        everySuspend { fixture.serviceDataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.serviceDataSource.deleteServicesInDb(listOf(staleId)) }
    }

    @Test
    fun `refresh does not call delete when nothing is stale`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val service = stubService(businessId)
        everySuspend { fixture.serviceDataSource.getServices(businessId) } returns listOf(service)
        everySuspend { fixture.serviceDataSource.getServiceIdsInDb(businessId) } returns listOf(service.id)
        everySuspend { fixture.groupDataSource.saveGroupsInDB(any()) } returns Unit
        everySuspend { fixture.serviceDataSource.saveServicesInDB(listOf(service)) } returns Unit
        everySuspend { fixture.serviceDataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.serviceDataSource.deleteServicesInDb(any()) }
    }

    @Test
    fun `refresh propagates a fetch error to the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val error = IllegalStateException("network down")
        everySuspend { fixture.serviceDataSource.getServices(businessId) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> { fixture.sut.refresh(businessId) }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) { fixture.serviceDataSource.getServiceIdsInDb(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.serviceDataSource.saveServicesInDB(any()) }
    }
}
