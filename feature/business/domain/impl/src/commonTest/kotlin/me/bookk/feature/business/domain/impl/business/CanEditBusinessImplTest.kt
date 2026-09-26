package me.bookk.feature.business.domain.impl.business

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
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.impl.stubBusiness
import me.bookk.feature.business.domain.impl.stubBusinessPermissions
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class CanEditBusinessImplTest {

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
        val dataSource = mock<BusinessDataSource>()
        val sut = CanEditBusinessImpl(dataSource)
    }

    @Test
    fun `returns true when the user can update the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessById(businessId) } returns stubBusiness(
            id = businessId,
            permissions = stubBusinessPermissions().copy(business = ResourcePermission(view = true, update = true))
        )

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertTrue(result)
    }

    @Test
    fun `returns false when the user can only view the business`() = runUnitTest {
        given()
        val fixture = Fixture()
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessById(businessId) } returns stubBusiness(
            id = businessId,
            permissions = stubBusinessPermissions().copy(business = ResourcePermission(view = true))
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
        val businessId = Uuid.random()
        everySuspend { fixture.dataSource.getBusinessById(businessId) } returns null

        whenn()
        val result = fixture.sut(businessId)

        then()
        assertFalse(result)
    }
}
