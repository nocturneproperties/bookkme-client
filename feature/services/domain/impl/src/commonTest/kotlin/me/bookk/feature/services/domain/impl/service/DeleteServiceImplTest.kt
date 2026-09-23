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
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.impl.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

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
        val fixture = Fixture()
        val service = stubService()
        everySuspend { fixture.dataSource.deleteService(service.businessId, service.id) } returns Unit
        everySuspend { fixture.dataSource.deleteServiceFromDB(service) } returns Unit

        whenn()
        fixture.sut(service)

        then()
        verifySuspend { fixture.dataSource.deleteService(service.businessId, service.id) }
    }

    @Test
    fun `calls deleteServiceFromDB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        everySuspend { fixture.dataSource.deleteService(any(), any()) } returns Unit
        everySuspend { fixture.dataSource.deleteServiceFromDB(service) } returns Unit

        whenn()
        fixture.sut(service)

        then()
        verifySuspend { fixture.dataSource.deleteServiceFromDB(service) }
    }
}
