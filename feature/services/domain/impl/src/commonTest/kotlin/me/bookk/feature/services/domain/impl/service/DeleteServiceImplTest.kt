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
        val dataSource = mock<ServiceDataSource>()
        val sut = DeleteServiceImpl(dataSource)
    }

    @Test
    fun `calls deleteService with businessId and id`() = runUnitTest {
        given()
        val sut = Fixture()
        val service = stubService()
        everySuspend { sut.dataSource.deleteService(service.businessId, service.id) } returns Unit
        everySuspend { sut.dataSource.deleteServiceFromDB(service) } returns Unit

        whenn()
        sut.sut(service)

        then()
        verifySuspend { sut.dataSource.deleteService(service.businessId, service.id) }
    }

    @Test
    fun `calls deleteServiceFromDB`() = runUnitTest {
        given()
        val sut = Fixture()
        val service = stubService()
        everySuspend { sut.dataSource.deleteService(any(), any()) } returns Unit
        everySuspend { sut.dataSource.deleteServiceFromDB(service) } returns Unit

        whenn()
        sut.sut(service)

        then()
        verifySuspend { sut.dataSource.deleteServiceFromDB(service) }
    }

    @Test
    fun `emits Deleted event`() = runUnitTest {
        given()
        val sut = Fixture()
        val service = stubService()
        everySuspend { sut.dataSource.deleteService(any(), any()) } returns Unit
        everySuspend { sut.dataSource.deleteServiceFromDB(any()) } returns Unit
        val events = mutableListOf<ServiceEvent>()
        val job = launch(Dispatchers.Unconfined) { serviceEvents.collect { events.add(it) } }

        whenn()
        sut.sut(service)

        then()
        job.cancel()
        assertTrue(events.any { it is ServiceEvent.Deleted })
    }
}
