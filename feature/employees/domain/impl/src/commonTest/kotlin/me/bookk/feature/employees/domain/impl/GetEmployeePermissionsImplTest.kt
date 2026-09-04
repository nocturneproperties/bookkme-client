package me.bookk.feature.employees.domain.impl

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
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetEmployeePermissionsImplTest {

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
        val dataSource = mock<EmployeeDataSource>()
        val sut = GetEmployeePermissionsImpl(dataSource)
    }

    @Test
    fun `returns employee permissions from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val id = Uuid.random()
        val permissions = stubBusinessPermissions()
        everySuspend { fixture.dataSource.getEmployeePermissions(businessId, id) } returns permissions

        whenn()
        val result = fixture.sut(businessId, id)

        then()
        assertEquals(permissions, result)
    }
}
