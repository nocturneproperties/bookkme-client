package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.services.domain.api.service.GetServices
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAssignableServicesImplTest {

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
        val getServices = mock<GetServices>()
        val sut = GetAssignableServicesImpl(getServices)
    }

    @Test
    fun `emits the business services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val services = listOf(stubService(), stubService())
        every { fixture.getServices.flow() } returns flowOf(services)

        whenn()
        val result = fixture.sut.flow().first()

        then()
        assertEquals(services, result)
    }

    @Test
    fun `refresh returns the freshly fetched business services`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val services = listOf(stubService(businessId))
        everySuspend { fixture.getServices.refresh(businessId) } returns services

        whenn()
        val result = fixture.sut.refresh(businessId)

        then()
        assertEquals(services, result)
    }
}
