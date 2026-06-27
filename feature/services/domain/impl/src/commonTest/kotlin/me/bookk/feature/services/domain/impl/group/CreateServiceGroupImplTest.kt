package me.bookk.feature.services.domain.impl.group

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
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
import me.bookk.feature.services.domain.api.group.CreateServiceGroup
import me.bookk.feature.services.domain.api.group.ServiceGroupEvent
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.group.serviceGroupEvents
import me.bookk.feature.services.domain.datasource.ServiceErrorCodes
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class CreateServiceGroupImplTest {

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
        val sut = CreateServiceGroupImpl(dataSource)
    }

    private fun stubGroup(businessId: Uuid = Uuid.random()) = ServiceGroup(
        id = Uuid.random(), businessId = businessId, name = "Coloring",
        createdAt = Instant.fromEpochMilliseconds(0)
    )

    @Test
    fun `returns created group`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubGroup()
        everySuspend { fixture.dataSource.createServiceGroup(input) } returns input
        everySuspend { fixture.dataSource.saveGroupInDB(input) } returns Unit

        whenn()
        val result = fixture.sut(input)

        then()
        assertEquals(input, result)
    }

    @Test
    fun `saves group in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubGroup()
        everySuspend { fixture.dataSource.createServiceGroup(input) } returns input
        everySuspend { fixture.dataSource.saveGroupInDB(input) } returns Unit

        whenn()
        fixture.sut(input)

        then()
        verifySuspend { fixture.dataSource.saveGroupInDB(input) }
    }

    @Test
    fun `emits Created event`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubGroup()
        everySuspend { fixture.dataSource.createServiceGroup(input) } returns input
        everySuspend { fixture.dataSource.saveGroupInDB(any()) } returns Unit
        val events = mutableListOf<ServiceGroupEvent>()
        val job = launch(Dispatchers.Unconfined) { serviceGroupEvents.collect { events.add(it) } }

        whenn()
        fixture.sut(input)

        then()
        job.cancel()
        assertTrue(events.any { it is ServiceGroupEvent.Created })
    }

    @Test
    fun `throws NameExists on BUSINESS_SERVICE_GROUP_EXISTS error`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubGroup()
        everySuspend { fixture.dataSource.createServiceGroup(input) } throws
            DomainError.BusinessError(ServiceErrorCodes.BUSINESS_SERVICE_GROUP_EXISTS, "msg")

        whenn()
        then()
        assertFailsWith<CreateServiceGroup.Error.NameExists> {
            fixture.sut(input)
        }
    }

    @Test
    fun `throws InvalidName on BUSINESS_SERVICE_GROUP_VALIDATION_ERROR`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubGroup()
        everySuspend { fixture.dataSource.createServiceGroup(input) } throws
            DomainError.BusinessError(ServiceErrorCodes.BUSINESS_SERVICE_GROUP_VALIDATION_ERROR, "msg")

        whenn()
        then()
        assertFailsWith<CreateServiceGroup.Error.InvalidName> {
            fixture.sut(input)
        }
    }
}
