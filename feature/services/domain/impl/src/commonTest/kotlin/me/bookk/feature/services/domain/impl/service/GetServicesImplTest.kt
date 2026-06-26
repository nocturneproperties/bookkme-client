package me.bookk.feature.services.domain.impl.service

import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.mockk
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
        val serviceDataSource = mockk<ServiceDataSource>()
        val groupDataSource = mockk<ServiceGroupDataSource>()
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
        coEvery { fixture.serviceDataSource.getServices(businessId) } returns listOf(newer, older)
        coJustRun { fixture.groupDataSource.saveGroupsInDB(any()) }
        coJustRun { fixture.serviceDataSource.saveServicesInDB(any()) }

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
        coEvery { fixture.serviceDataSource.getServices(businessId) } returns listOf(service)
        coJustRun { fixture.groupDataSource.saveGroupsInDB(any()) }
        coJustRun { fixture.serviceDataSource.saveServicesInDB(listOf(service)) }

        whenn()
        fixture.sut(businessId)

        then()
        coVerify { fixture.serviceDataSource.saveServicesInDB(listOf(service)) }
        coVerify { fixture.groupDataSource.saveGroupsInDB(any()) }
    }

    @Test
    fun `cached calls onResultAvailable with DB then remote when DB non-empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val cached = listOf(stubService(businessId))
        val remote = listOf(stubService(businessId))
        coEvery { fixture.serviceDataSource.getServicesFromDb(businessId) } returns cached
        coEvery { fixture.serviceDataSource.getServices(businessId) } returns remote
        coJustRun { fixture.groupDataSource.saveGroupsInDB(any()) }
        coJustRun { fixture.serviceDataSource.saveServicesInDB(any()) }
        val received = mutableListOf<List<Service>>()

        whenn()
        fixture.fixture.cached(businessId) { received.add(it) }

        then()
        assertEquals(2, received.size)
    }

    @Test
    fun `cached skips DB callback when DB is empty`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val remote = listOf(stubService(businessId))
        coEvery { fixture.serviceDataSource.getServicesFromDb(businessId) } returns emptyList()
        coEvery { fixture.serviceDataSource.getServices(businessId) } returns remote
        coJustRun { fixture.groupDataSource.saveGroupsInDB(any()) }
        coJustRun { fixture.serviceDataSource.saveServicesInDB(any()) }
        val received = mutableListOf<List<Service>>()

        whenn()
        fixture.fixture.cached(businessId) { received.add(it) }

        then()
        assertEquals(1, received.size)
    }
}
