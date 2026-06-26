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
        val dataSource = mockk<ServiceDataSource>()
        val sut = EditServiceImpl(dataSource)
    }

    @Test
    fun `returns edited service from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val service = stubService()
        val updated = service.copy(name = "Color")
        coEvery { fixture.dataSource.editService(service) } returns updated
        coJustRun { fixture.dataSource.saveServiceInDB(updated) }

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
        coEvery { fixture.dataSource.editService(service) } returns updated
        coJustRun { fixture.dataSource.saveServiceInDB(updated) }

        whenn()
        fixture.sut(service)

        then()
        coVerify { fixture.dataSource.saveServiceInDB(updated) }
    }
}
