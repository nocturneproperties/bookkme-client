package me.bookk.feature.employees.domain.impl

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
import kotlinx.datetime.TimeZone
import library.money.api.Currency
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class ObserveCurrentBusinessIdImplTest {

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
        val sut = ObserveCurrentBusinessIdImpl(observeDashboardBusinessChanges)
    }

    private fun stubBusiness(id: Uuid = Uuid.random()) = Business(
        id = id,
        name = "Test Business",
        description = "",
        address = "",
        location = null,
        currency = Currency("USD"),
        timeZone = TimeZone.UTC,
        socials = emptyMap(),
        schedule = WorkingSchedule(),
        permissions = BusinessPermissions(
            business = ResourcePermission(),
            employees = ResourcePermission(),
            clients = ResourcePermission(),
            services = ResourcePermission(),
            appointments = ResourcePermission()
        )
    )

    @Test
    fun `emits business id when business is present`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val business = stubBusiness(id = businessId)
        every { fixture.observeDashboardBusinessChanges.invoke() } returns flowOf(business)

        whenn()
        val result = fixture.sut().first()

        then()
        assertEquals(businessId, result)
    }

    @Test
    fun `emits null when business is null`() = runUnitTest {
        given()
        val fixture = Fixture()
        every { fixture.observeDashboardBusinessChanges.invoke() } returns flowOf(null)

        whenn()
        val result = fixture.sut().first()

        then()
        assertNull(result)
    }
}
