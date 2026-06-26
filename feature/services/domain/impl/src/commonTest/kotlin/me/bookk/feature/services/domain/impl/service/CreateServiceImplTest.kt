package me.bookk.feature.services.domain.impl.service

import dev.mokkery.answering.returns
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
        val dataSource = mock<ServiceDataSource>()
        val sut = CreateServiceImpl(dataSource)
    }

    @Test
    fun `returns created service`() = runUnitTest {
        given()
        val sut = Fixture()
        val input = stubService()
        val created = input.copy(id = kotlin.uuid.Uuid.random())
        everySuspend { sut.dataSource.createService(input) } returns created
        everySuspend { sut.dataSource.saveServiceInDB(created) } returns Unit

        whenn()
        val result = sut.sut(input)

        then()
        assertEquals(created, result)
    }

    @Test
    fun `saves created service in DB`() = runUnitTest {
        given()
        val sut = Fixture()
        val input = stubService()
        everySuspend { sut.dataSource.createService(input) } returns input
        everySuspend { sut.dataSource.saveServiceInDB(input) } returns Unit

        whenn()
        sut.sut(input)

        then()
        verifySuspend { sut.dataSource.saveServiceInDB(input) }
    }

    @Test
    fun `emits Created event`() = runUnitTest {
        given()
        val sut = Fixture()
        val input = stubService()
        everySuspend { sut.dataSource.createService(input) } returns input
        everySuspend { sut.dataSource.saveServiceInDB(any()) } returns Unit
        val events = mutableListOf<ServiceEvent>()
        val job = launch(Dispatchers.Unconfined) { serviceEvents.collect { events.add(it) } }

        whenn()
        sut.sut(input)

        then()
        job.cancel()
        assertTrue(events.any { it is ServiceEvent.Created })
    }
}
