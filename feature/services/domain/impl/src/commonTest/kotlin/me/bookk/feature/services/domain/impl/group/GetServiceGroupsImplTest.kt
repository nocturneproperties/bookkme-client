package me.bookk.feature.services.domain.impl.group

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
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetServiceGroupsImplTest {

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
        val dataSource = mock<ServiceGroupDataSource>()
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges>()
        val sut = GetServiceGroupsImpl(dataSource, observeDashboardBusinessChanges)
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

    private fun stubGroup(businessId: Uuid, createdAtMs: Long = 0L) = ServiceGroup(
        id = Uuid.random(), businessId = businessId, name = "Group $createdAtMs",
        createdAt = Instant.fromEpochMilliseconds(createdAtMs)
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
    fun `flow emits the db groups sorted by createdAt for the current dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        val older = stubGroup(business.id, 1000L)
        val newer = stubGroup(business.id, 2000L)
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.dataSource.observeServiceGroupsDBChanges(business.id) } returns flowOf(listOf(newer, older))

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
        val firstGroups = listOf(stubGroup(firstBusiness.id))
        val secondGroups = listOf(stubGroup(secondBusiness.id))
        businesses.tryEmit(firstBusiness)
        every { fixture.observeDashboardBusinessChanges() } returns businesses
        every { fixture.dataSource.observeServiceGroupsDBChanges(firstBusiness.id) } returns flowOf(firstGroups)
        every { fixture.dataSource.observeServiceGroupsDBChanges(secondBusiness.id) } returns flowOf(secondGroups)
        val results = mutableListOf<List<ServiceGroup>>()
        val job = launch(Dispatchers.Unconfined) {
            fixture.sut.flow().collect { results.add(it) }
        }

        whenn()
        businesses.emit(secondBusiness)

        then()
        job.cancel()
        assertEquals(firstGroups, results.first())
        assertEquals(secondGroups, results.last())
    }

    @Test
    fun `flow never triggers a network fetch as a side effect of being collected`() = runUnitTest {
        given()
        val fixture = Fixture()
        val business = stubBusiness()
        every { fixture.observeDashboardBusinessChanges() } returns flowOf(business)
        every { fixture.dataSource.observeServiceGroupsDBChanges(business.id) } returns flowOf(emptyList())

        whenn()
        fixture.sut.flow().first()

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getServiceGroups(any()) }
    }

    @Test
    fun `refresh fetches groups from remote, saves them, and returns them sorted by createdAt`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val older = stubGroup(businessId, 1000L)
        val newer = stubGroup(businessId, 2000L)
        everySuspend { fixture.dataSource.getServiceGroups(businessId) } returns listOf(newer, older)
        everySuspend { fixture.dataSource.getServiceGroupIdsInDb(businessId) } returns listOf(older.id, newer.id)
        everySuspend { fixture.dataSource.saveGroupsInDB(any()) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(listOf(older, newer), result)
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveGroupsInDB(listOf(newer, older)) }
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.saveLastSyncedAt(businessId) }
    }

    @Test
    fun `refresh deletes local groups that are no longer present remotely`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val stillPresent = stubGroup(businessId)
        val remote = listOf(stillPresent)
        val staleId = Uuid.random()
        everySuspend { fixture.dataSource.getServiceGroups(businessId) } returns remote
        everySuspend { fixture.dataSource.getServiceGroupIdsInDb(businessId) } returns listOf(stillPresent.id, staleId)
        everySuspend { fixture.dataSource.deleteGroupsInDB(listOf(staleId)) } returns Unit
        everySuspend { fixture.dataSource.saveGroupsInDB(remote) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(1)) { fixture.dataSource.deleteGroupsInDB(listOf(staleId)) }
    }

    @Test
    fun `refresh does not call delete when nothing is stale`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val group = stubGroup(businessId)
        everySuspend { fixture.dataSource.getServiceGroups(businessId) } returns listOf(group)
        everySuspend { fixture.dataSource.getServiceGroupIdsInDb(businessId) } returns listOf(group.id)
        everySuspend { fixture.dataSource.saveGroupsInDB(listOf(group)) } returns Unit
        everySuspend { fixture.dataSource.saveLastSyncedAt(businessId) } returns Unit

        whenn()
        fixture.sut.refresh(businessId)

        then()
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.deleteGroupsInDB(any()) }
    }

    @Test
    fun `refresh propagates a fetch error to the caller`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val error = IllegalStateException("network down")
        everySuspend { fixture.dataSource.getServiceGroups(businessId) } throws error

        whenn()
        val thrown = assertFailsWith<IllegalStateException> { fixture.sut.refresh(businessId) }

        then()
        assertEquals(error, thrown)
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.getServiceGroupIdsInDb(any()) }
        verifySuspend(VerifyMode.exactly(0)) { fixture.dataSource.saveGroupsInDB(any()) }
    }
}
