package me.bookk.feature.services.domain.impl.service

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.services.domain.api.service.GetService
import me.bookk.feature.services.domain.datasource.ServiceDataSource
import me.bookk.feature.services.domain.impl.stubService
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class GetServiceImplTest {

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
        val sut = GetServiceImpl(dataSource)
    }

    @Test
    fun `returns service stored in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        everySuspend { fixture.dataSource.getServiceFromDb(service.id) } returns service

        whenn()
        val result = fixture.sut(service.id)

        then()
        assertEquals(service, result)
    }

    @Test
    fun `throws not found when service is absent in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        everySuspend { fixture.dataSource.getServiceFromDb(service.id) } returns null

        whenn()
        val result = runCatching { fixture.sut(service.id) }

        then()
        assertFailsWith<GetService.Error.NotFound> { result.getOrThrow() }
    }
}
