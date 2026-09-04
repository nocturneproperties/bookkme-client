package me.bookk.feature.business.domain.impl.business

import dev.mokkery.answering.returns
import dev.mokkery.answering.throws
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.DashboardFeature
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.business.domain.api.plugin.IsAppointmentsPluginEnabled
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import me.bookk.feature.business.domain.impl.stubBusiness
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
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

    private val noPermissions = BusinessPermissions(
        business = ResourcePermission(),
        employees = ResourcePermission(),
        clients = ResourcePermission(),
        services = ResourcePermission(),
        appointments = ResourcePermission()
    )

    private val fullPermissions = BusinessPermissions(
        business = ResourcePermission(view = true),
        employees = ResourcePermission(view = true),
        clients = ResourcePermission(view = true),
        services = ResourcePermission(view = true),
        appointments = ResourcePermission(view = true)
    )

    private class Fixture(businessId: Uuid, permissions: BusinessPermissions) {
        val isAppointmentsPluginEnabled = mock<IsAppointmentsPluginEnabled>()
        val businessDataSource = mock<BusinessDataSource>()
        val sut = GetAvailableDashboardFeaturesImpl(isAppointmentsPluginEnabled, businessDataSource)
        val business = stubBusiness(id = businessId, permissions = permissions)

        init {
            everySuspend { businessDataSource.getDashboardBusinessId() } returns businessId
            everySuspend { businessDataSource.getBusinessById(businessId) } returns business
            everySuspend { businessDataSource.saveDashboardFeatures(any(), any()) } returns Unit
        }
    }

    @Test
    fun `BUSINESS feature included when business view permission is granted`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val fixture = Fixture(businessId, fullPermissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } returns false

        whenn()
        val result = fixture.sut()

        then()
        assertTrue(DashboardFeature.BUSINESS in result)
    }

    @Test
    fun `BUSINESS feature excluded when business view permission is not granted`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val fixture = Fixture(businessId, noPermissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } returns false

        whenn()
        val result = fixture.sut()

        then()
        assertFalse(DashboardFeature.BUSINESS in result)
    }

    @Test
    fun `EMPLOYEES feature included only when employees view permission is granted`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val permissions = noPermissions.copy(employees = ResourcePermission(view = true))
        val fixture = Fixture(businessId, permissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } returns false

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(setOf(DashboardFeature.EMPLOYEES), result)
    }

    @Test
    fun `CLIENTS feature included only when clients view permission is granted`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val permissions = noPermissions.copy(clients = ResourcePermission(view = true))
        val fixture = Fixture(businessId, permissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } returns false

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(setOf(DashboardFeature.CLIENTS), result)
    }

    @Test
    fun `SERVICES feature included only when services view permission is granted`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val permissions = noPermissions.copy(services = ResourcePermission(view = true))
        val fixture = Fixture(businessId, permissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } returns false

        whenn()
        val result = fixture.sut()

        then()
        assertEquals(setOf(DashboardFeature.SERVICES), result)
    }

    @Test
    fun `APPOINTMENTS feature included when plugin is enabled and view permission is granted`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val fixture = Fixture(businessId, fullPermissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(businessId) } returns true

        whenn()
        val result = fixture.sut()

        then()
        assertTrue(DashboardFeature.APPOINTMENTS in result)
    }

    @Test
    fun `APPOINTMENTS feature not included when plugin is disabled`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val fixture = Fixture(businessId, fullPermissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(businessId) } returns false

        whenn()
        val result = fixture.sut()

        then()
        assertFalse(DashboardFeature.APPOINTMENTS in result)
    }

    @Test
    fun `APPOINTMENTS feature not included when view permission is not granted`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val fixture = Fixture(businessId, noPermissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(businessId) } returns true

        whenn()
        val result = fixture.sut()

        then()
        assertFalse(DashboardFeature.APPOINTMENTS in result)
    }

    @Test
    fun `returns empty set when no dashboard business id`() = runUnitTest {
        given()
        val fixture = Fixture(Uuid.random(), fullPermissions)
        everySuspend { fixture.businessDataSource.getDashboardBusinessId() } returns null

        whenn()
        val result = fixture.sut()

        then()
        assertTrue(result.isEmpty())
    }

    @Test
    fun `APPOINTMENTS feature not included when plugin check throws`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val fixture = Fixture(businessId, fullPermissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } throws RuntimeException("error")

        whenn()
        val result = fixture.sut()

        then()
        assertFalse(DashboardFeature.APPOINTMENTS in result)
    }

    @Test
    fun `saves fetched features to the data source keyed by business id`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val permissions = noPermissions.copy(clients = ResourcePermission(view = true))
        val fixture = Fixture(businessId, permissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } returns false

        whenn()
        fixture.sut()

        then()
        verifySuspend {
            fixture.businessDataSource.saveDashboardFeatures(businessId, setOf(DashboardFeature.CLIENTS))
        }
    }

    @Test
    fun `cached emits the cached value before the fetched value when previously synced`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val fixture = Fixture(businessId, fullPermissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } returns false
        everySuspend {
            fixture.businessDataSource.getDashboardFeatures(businessId)
        } returns setOf(DashboardFeature.CLIENTS)
        val results = mutableListOf<Set<DashboardFeature>>()

        whenn()
        fixture.sut.cached(businessId) { results.add(it) }

        then()
        assertEquals(setOf(DashboardFeature.CLIENTS), results.first())
        assertTrue(DashboardFeature.BUSINESS in results.last())
    }

    @Test
    fun `cached skips the cached emission when never synced before`() = runUnitTest {
        given()
        val businessId = Uuid.random()
        val fixture = Fixture(businessId, fullPermissions)
        everySuspend { fixture.isAppointmentsPluginEnabled(any()) } returns false
        everySuspend { fixture.businessDataSource.getDashboardFeatures(businessId) } returns null
        val results = mutableListOf<Set<DashboardFeature>>()

        whenn()
        fixture.sut.cached(businessId) { results.add(it) }

        then()
        assertEquals(1, results.size)
    }
}
