package me.bookk.feature.services.domain.impl.group

import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.services.domain.api.group.ServiceGroupEvent
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.group.serviceGroupEvents
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteServiceGroupImplTest {

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
        val dataSource = mockk<ServiceGroupDataSource>()
        val sut = DeleteServiceGroupImpl(dataSource)
    }

    private fun stubGroup() = ServiceGroup(
        id = Uuid.random(), businessId = Uuid.random(), name = "Coloring",
        createdAt = Instant.fromEpochMilliseconds(0)
    )

    @Test
    fun `calls deleteServiceGroup with businessId and id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val group = stubGroup()
        coJustRun { fixture.dataSource.deleteServiceGroup(group.businessId, group.id) }
        coJustRun { fixture.dataSource.deleteGroupFromDB(group) }

        whenn()
        fixture.sut(group)

        then()
        coVerify { fixture.dataSource.deleteServiceGroup(group.businessId, group.id) }
    }

    @Test
    fun `calls deleteGroupFromDB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val group = stubGroup()
        coJustRun { fixture.dataSource.deleteServiceGroup(any(), any()) }
        coJustRun { fixture.dataSource.deleteGroupFromDB(group) }

        whenn()
        fixture.sut(group)

        then()
        coVerify { fixture.dataSource.deleteGroupFromDB(group) }
    }

    @Test
    fun `emits Deleted event`() = runUnitTest {
        given()
        val fixture = Fixture()
        val group = stubGroup()
        coJustRun { fixture.dataSource.deleteServiceGroup(any(), any()) }
        coJustRun { fixture.dataSource.deleteGroupFromDB(any()) }
        val events = mutableListOf<ServiceGroupEvent>()
        val job = launch(Dispatchers.Unconfined) { serviceGroupEvents.collect { events.add(it) } }

        whenn()
        fixture.sut(group)

        then()
        job.cancel()
        assertTrue(events.any { it is ServiceGroupEvent.Deleted })
    }
}
