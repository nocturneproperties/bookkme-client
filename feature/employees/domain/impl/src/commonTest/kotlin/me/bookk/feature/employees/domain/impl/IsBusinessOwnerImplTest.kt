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
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class IsBusinessOwnerImplTest {

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
        val sut = IsBusinessOwnerImpl(observeUserBusinessesChanges)

        fun stubBusinesses(vararg businesses: Business) {
            every { observeUserBusinessesChanges.invoke() } returns flowOf(businesses.toList())
        }
    }

    @Test
    fun `returns true when the employee user owns the employee business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        fixture.stubBusinesses(stubBusiness(id = employee.businessId, ownerId = employee.userId))

        whenn()
        val result = fixture.sut(employee)

        then()
        assertTrue(result)
    }

    @Test
    fun `returns false when another user owns the employee business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        fixture.stubBusinesses(stubBusiness(id = employee.businessId, ownerId = Uuid.random()))

        whenn()
        val result = fixture.sut(employee)

        then()
        assertFalse(result)
    }

    @Test
    fun `returns false when the employee user owns a different business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        fixture.stubBusinesses(
            stubBusiness(id = Uuid.random(), ownerId = employee.userId),
            stubBusiness(id = employee.businessId, ownerId = Uuid.random())
        )

        whenn()
        val result = fixture.sut(employee)

        then()
        assertFalse(result)
    }

    @Test
    fun `returns false when the employee business is not cached`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        fixture.stubBusinesses()

        whenn()
        val result = fixture.sut(employee)

        then()
        assertFalse(result)
    }

    @Test
    fun `returns false when the cached business has no owner yet`() = runUnitTest {
        given()
        val fixture = Fixture()
        val employee = stubEmployee()
        fixture.stubBusinesses(stubBusiness(id = employee.businessId, ownerId = null))

        whenn()
        val result = fixture.sut(employee)

        then()
        assertFalse(result)
    }
}
