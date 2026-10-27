package me.bookk.feature.services.domain.impl.service

import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
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
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class EditServiceImplTest {

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
        val sut = EditServiceImpl(dataSource)
    }

    @Test
    fun `returns edited service from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        val updated = service.copy(name = "Color")
        everySuspend { fixture.dataSource.editService(service) } returns updated
        everySuspend { fixture.dataSource.saveServiceInDB(updated) } returns Unit

        whenn()
        val result = fixture.sut(service)

        then()
        assertEquals(updated, result)
    }

    @Test
    fun `saves edited service in DB`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        val updated = service.copy(name = "Color")
        everySuspend { fixture.dataSource.editService(service) } returns updated
        everySuspend { fixture.dataSource.saveServiceInDB(updated) } returns Unit

        whenn()
        fixture.sut(service)

        then()
        verifySuspend { fixture.dataSource.saveServiceInDB(updated) }
    }
}
