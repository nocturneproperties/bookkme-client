package me.bookk.feature.services.domain.impl.service

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
import me.bookk.feature.services.domain.api.service.ServiceEvent
import me.bookk.feature.services.domain.api.service.serviceEvents
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.impl.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DeleteServiceImplTest {

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
        val dataSource = mockk<ServiceDataSource>()
        val sut = DeleteServiceImpl(dataSource)
    }

    @Test
    fun `calls deleteService with businessId and id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        coJustRun { fixture.dataSource.deleteService(service.businessId, service.id) }
        coJustRun { fixture.dataSource.deleteServiceFromDB(service) }

        whenn()
        fixture.sut(service)

        then()
        coVerify { fixture.dataSource.deleteService(service.businessId, service.id) }
    }

    @Test
    fun `calls deleteServiceFromDB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        coJustRun { fixture.dataSource.deleteService(any(), any()) }
        coJustRun { fixture.dataSource.deleteServiceFromDB(service) }

        whenn()
        fixture.sut(service)

        then()
        coVerify { fixture.dataSource.deleteServiceFromDB(service) }
    }

    @Test
    fun `emits Deleted event`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        coJustRun { fixture.dataSource.deleteService(any(), any()) }
        coJustRun { fixture.dataSource.deleteServiceFromDB(any()) }
        val events = mutableListOf<ServiceEvent>()
        val job = launch(Dispatchers.Unconfined) { serviceEvents.collect { events.add(it) } }

        whenn()
        fixture.sut(service)

        then()
        job.cancel()
        assertTrue(events.any { it is ServiceEvent.Deleted })
    }
}
