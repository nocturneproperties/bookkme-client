package me.bookk.feature.services.domain.impl.service

import io.mockk.coEvery
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
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CreateServiceImplTest {

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
        val sut = CreateServiceImpl(dataSource)
    }

    @Test
    fun `returns created service`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubService()
        val created = input.copy(id = kotlin.uuid.Uuid.random())
        coEvery { fixture.dataSource.createService(input) } returns created
        coJustRun { fixture.dataSource.saveServiceInDB(created) }

        whenn()
        val result = fixture.sut(input)

        then()
        assertEquals(created, result)
    }

    @Test
    fun `saves created service in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubService()
        coEvery { fixture.dataSource.createService(input) } returns input
        coJustRun { fixture.dataSource.saveServiceInDB(input) }

        whenn()
        fixture.sut(input)

        then()
        coVerify { fixture.dataSource.saveServiceInDB(input) }
    }

    @Test
    fun `emits Created event`() = runUnitTest {
        given()
        val fixture = Fixture()
        val input = stubService()
        coEvery { fixture.dataSource.createService(input) } returns input
        coJustRun { fixture.dataSource.saveServiceInDB(any()) }
        val events = mutableListOf<ServiceEvent>()
        val job = launch(Dispatchers.Unconfined) { serviceEvents.collect { events.add(it) } }

        whenn()
        fixture.sut(input)

        then()
        job.cancel()
        assertTrue(events.any { it is ServiceEvent.Created })
    }
}
