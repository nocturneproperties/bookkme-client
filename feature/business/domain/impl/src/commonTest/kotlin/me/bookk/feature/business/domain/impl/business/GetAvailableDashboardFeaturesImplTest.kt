package me.bookk.feature.business.domain.impl.business

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
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.impl.stubBusiness
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.uuid.Uuid

@OptIn(ExperimentalCoroutinesApi::class)
class GetAvailableDashboardFeaturesImplTest {

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
        val isAppointmentsPluginEnabled = mock<IsAppointmentsPluginEnabled>()
        val businessDataSource = mock<BusinessDataSource>()
        val sut = GetAvailableDashboardFeaturesImpl(isAppointmentsPluginEnabled, businessDataSource)
        val businessId = Uuid.random()
        val business = stubBusiness(id = businessId)

        init {
            everySuspend { businessDataSource.getDashboardBusinessId() } returns businessId
            everySuspend { businessDataSource.getBusinessById(businessId) } returns business
        }
    }

    @Test
    fun `APPOINTMENTS feature included when plugin is enabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.isAppointmentsPluginEnabled(fixture.businessId) } returns true

        whenn()
        val result = fixture.sut()

        then()
        assertTrue(DashboardFeature.APPOINTMENTS in result)
    }

    @Test
    fun `APPOINTMENTS feature not included when plugin is disabled`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.isAppointmentsPluginEnabled(fixture.businessId) } returns false

        whenn()
        val result = fixture.sut()

        then()
        assertFalse(DashboardFeature.APPOINTMENTS in result)
    }

    @Test
    fun `BUSINESS feature always included when business exists`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } returns false

        whenn()
        val result = fixture.sut()

        then()
        assertTrue(DashboardFeature.BUSINESS in result)
    }

    @Test
    fun `returns empty set when no dashboard business id`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.businessDataSource.getDashboardBusinessId() } returns null

        whenn()
        val result = fixture.sut()

        then()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `APPOINTMENTS feature not included when plugin check throws`() = runUnitTest {
        given()
        val fixture = Fixture()
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } throws RuntimeException("error")

        whenn()
        val result = fixture.sut()

        then()
        assertFalse(DashboardFeature.APPOINTMENTS in result)
    }
}
