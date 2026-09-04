package me.bookk.feature.employees.domain.impl

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
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
import me.bookk.feature.business.domain.api.entity.BusinessResource
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.employees.domain.api.SetEmployeePermission
import me.bookk.feature.employees.domain.datasource.EmployeeDataSource
import me.bookk.feature.employees.domain.datasource.EmployeeErrorCodes
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.uuid.Uuid
import me.bookk.core.domain.entity.Error as DomainError

@OptIn(ExperimentalCoroutinesApi::class)
class SetEmployeePermissionImplTest {

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
        val sut = SetEmployeePermissionImpl(dataSource)
    }

    @Test
    fun `returns updated permissions from datasource`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val id = Uuid.random()
        val permission = ResourcePermission(view = true, update = false, delete = false)
        val permissions = stubBusinessPermissions()
        everySuspend {
            fixture.dataSource.setEmployeePermission(businessId, id, BusinessResource.CLIENTS, permission)
        } returns permissions

        whenn()
        val result = fixture.sut(businessId, id, BusinessResource.CLIENTS, permission)

        then()
        assertEquals(permissions, result)
    }

    @Test
    fun `throws InsufficientGrant on BUSINESS_INSUFFICIENT_GRANT_PERMISSION`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        val id = Uuid.random()
        everySuspend {
            fixture.dataSource.setEmployeePermission(businessId, id, any(), any())
        } throws DomainError.BusinessError(EmployeeErrorCodes.BUSINESS_INSUFFICIENT_GRANT_PERMISSION, "msg")

        whenn()
        then()
        assertFailsWith<SetEmployeePermission.Error.InsufficientGrant> {
            fixture.sut(businessId, id, BusinessResource.APPOINTMENTS, ResourcePermission())
        }
    }
}
