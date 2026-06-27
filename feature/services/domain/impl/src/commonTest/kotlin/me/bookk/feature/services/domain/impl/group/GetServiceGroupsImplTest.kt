package me.bookk.feature.services.domain.impl.group

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val sut = GetServiceGroupsImpl(dataSource)
    }

    private fun stubGroup(businessId: Uuid, createdAtMs: Long = 0L) = ServiceGroup(
        id = Uuid.random(), businessId = businessId, name = "Group $createdAtMs",
        createdAt = Instant.fromEpochMilliseconds(createdAtMs)
    )

    @Test
    fun `returns groups sorted by createdAt`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val older = stubGroup(businessId, 1000L)
        val newer = stubGroup(businessId, 2000L)
        everySuspend { fixture.dataSource.getServiceGroups(businessId) } returns listOf(newer, older)
        everySuspend { fixture.dataSource.saveGroupsInDB(any()) } returns Unit

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(older, result[0])
        assertEquals(newer, result[1])
    }

    @Test
    fun `saves groups in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val groups = listOf(stubGroup(businessId))
        everySuspend { fixture.dataSource.getServiceGroups(businessId) } returns groups
        everySuspend { fixture.dataSource.saveGroupsInDB(groups) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend { fixture.dataSource.saveGroupsInDB(groups) }
    }

    @Test
    fun `cached emits groups when non-empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val groups = listOf(stubGroup(businessId))
        everySuspend { fixture.dataSource.getServiceGroups(businessId) } returns groups
        everySuspend { fixture.dataSource.saveGroupsInDB(any()) } returns Unit
        val received = mutableListOf<List<ServiceGroup>>()

        whenn()
        fixture.sut.cached(businessId) { received.add(it) }

        then()
        assertEquals(2, received.size)
    }
}
