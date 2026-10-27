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
        val dataSource = mock<ServiceGroupDataSource>()
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
        everySuspend { fixture.dataSource.deleteServiceGroup(group.businessId, group.id) } returns Unit
        everySuspend { fixture.dataSource.deleteGroupFromDB(group) } returns Unit

        whenn()
        fixture.sut(group)

        then()
        verifySuspend { fixture.dataSource.deleteServiceGroup(group.businessId, group.id) }
    }

    @Test
    fun `calls deleteGroupFromDB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val group = stubGroup()
        everySuspend { fixture.dataSource.deleteServiceGroup(any(), any()) } returns Unit
        everySuspend { fixture.dataSource.deleteGroupFromDB(group) } returns Unit

        whenn()
        fixture.sut(group)

        then()
        verifySuspend { fixture.dataSource.deleteGroupFromDB(group) }
    }
}
