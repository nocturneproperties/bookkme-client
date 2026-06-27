package me.bookk.feature.services.domain.impl.service

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
import me.bookk.feature.services.domain.api.service.entity.Service
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.datasource.ServiceGroupDataSource
import me.bookk.feature.services.domain.impl.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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
        val sut = GetServicesImpl(serviceDataSource, groupDataSource)
    }

    private fun stubTimedService(businessId: Uuid, createdAtMs: Long) =
        stubService(businessId).let { s ->
            Service(
                id = s.id, businessId = s.businessId, group = s.group, name = s.name,
                duration = s.duration, price = s.price, isAvailable = s.isAvailable,
                createdAt = Instant.fromEpochMilliseconds(createdAtMs)
            )
        }

    @Test
    fun `returns services sorted by createdAt`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val older = stubTimedService(businessId, 1000L)
        val newer = stubTimedService(businessId, 2000L)
        everySuspend { fixture.serviceDataSource.getServices(businessId) } returns listOf(newer, older)
        everySuspend { fixture.groupDataSource.saveGroupsInDB(any()) } returns Unit
        everySuspend { fixture.serviceDataSource.saveServicesInDB(any()) } returns Unit

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertEquals(older, result[0])
        assertEquals(newer, result[1])
    }

    @Test
    fun `saves services and their groups in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val service = stubService(businessId)
        everySuspend { fixture.serviceDataSource.getServices(businessId) } returns listOf(service)
        everySuspend { fixture.groupDataSource.saveGroupsInDB(any()) } returns Unit
        everySuspend { fixture.serviceDataSource.saveServicesInDB(listOf(service)) } returns Unit

        whenn()
        fixture.sut(businessId)

        then()
        verifySuspend { fixture.serviceDataSource.saveServicesInDB(listOf(service)) }
        verifySuspend { fixture.groupDataSource.saveGroupsInDB(any()) }
    }

    @Test
    fun `cached calls onResultAvailable with DB then remote when DB non-empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val cached = listOf(stubService(businessId))
        val remote = listOf(stubService(businessId))
        everySuspend { fixture.serviceDataSource.getServicesFromDb(businessId) } returns cached
        everySuspend { fixture.serviceDataSource.getServices(businessId) } returns remote
        everySuspend { fixture.groupDataSource.saveGroupsInDB(any()) } returns Unit
        everySuspend { fixture.serviceDataSource.saveServicesInDB(any()) } returns Unit
        val received = mutableListOf<List<Service>>()

        whenn()
        fixture.sut.cached(businessId) { received.add(it) }

        then()
        assertEquals(2, received.size)
    }

    @Test
    fun `cached skips DB callback when DB is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val remote = listOf(stubService(businessId))
        everySuspend { fixture.serviceDataSource.getServicesFromDb(businessId) } returns emptyList()
        everySuspend { fixture.serviceDataSource.getServices(businessId) } returns remote
        everySuspend { fixture.groupDataSource.saveGroupsInDB(any()) } returns Unit
        everySuspend { fixture.serviceDataSource.saveServicesInDB(any()) } returns Unit
        val received = mutableListOf<List<Service>>()

        whenn()
        fixture.sut.cached(businessId) { received.add(it) }

        then()
        assertEquals(1, received.size)
    }
}
