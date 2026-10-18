package me.bookk.feature.appointments.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
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
import me.bookk.feature.appointments.domain.api.entity.CurrentBusiness
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveCurrentBusinessImplTest {

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
        val observeDashboardBusinessChanges = mock<ObserveDashboardBusinessChanges>()
        val sut = ObserveCurrentBusinessImpl(observeDashboardBusinessChanges)
    }

    @Test
    fun `emits business id with the caller's employee id`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val employeeId = Uuid.random()
        every { fixture.observeDashboardBusinessChanges.invoke() } returns flowOf(stubBusiness(id = businessId, employeeId = employeeId))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(CurrentBusiness(id = businessId, employeeId = employeeId), result)
    }

    @Test
    fun `emits no employee id when the caller is not an active employee`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        every { fixture.observeDashboardBusinessChanges.invoke() } returns flowOf(stubBusiness(id = businessId, employeeId = null))

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(CurrentBusiness(id = businessId, employeeId = null), result)
    }

    @Test
    fun `emits null when there is no dashboard business`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges.invoke() } returns flowOf(null)

        whenn()
        val result = fixture.sut().first()

        then()
        assertNull(result)
    }
}
