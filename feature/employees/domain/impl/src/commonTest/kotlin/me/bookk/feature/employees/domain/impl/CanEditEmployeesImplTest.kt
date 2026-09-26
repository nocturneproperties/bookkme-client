package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CanEditEmployeesImplTest {

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
        val observeUserBusinessesChanges = mock<ObserveUserBusinessesChanges>()
        val sut = CanEditEmployeesImpl(observeUserBusinessesChanges)

        fun stubBusinesses(vararg businesses: Business) {
            every { observeUserBusinessesChanges.invoke() } returns flowOf(businesses.toList())
        }
    }

    @Test
    fun `returns true when the user can update employees of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(stubBusiness(id = businessId, permissions = employeesPermission(update = true)))

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertTrue(result)
    }

    @Test
    fun `returns false when the user can only view employees of the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(stubBusiness(id = businessId, permissions = employeesPermission(update = false)))

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertFalse(result)
    }

    @Test
    fun `returns false when the update permission belongs to a different business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        fixture.stubBusinesses(
            stubBusiness(id = Uuid.random(), permissions = employeesPermission(update = true)),
            stubBusiness(id = businessId, permissions = employeesPermission(update = false))
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertFalse(result)
    }

    @Test
    fun `returns false when the business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        fixture.stubBusinesses()

        whenn()
        val result = fixture.sut(Uuid.random())

        then()
        assertFalse(result)
    }

    private fun employeesPermission(update: Boolean) = stubBusinessPermissions().copy(
        employees = ResourcePermission(view = true, update = update, delete = false)
    )
}
